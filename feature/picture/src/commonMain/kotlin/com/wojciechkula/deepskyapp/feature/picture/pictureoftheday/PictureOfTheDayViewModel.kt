package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import com.wojciechkula.deepskyapp.core.common.NetworkMonitor
import com.wojciechkula.deepskyapp.core.mvvm.StateActionsViewModel
import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.interactor.AddFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.CheckIfPictureIsFavouriteInteractor
import com.wojciechkula.deepskyapp.domain.interactor.DeleteFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetPictureOfTheDayInteractor
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.FavouritePressed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Paused
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Resumed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.RetryPressed
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

private const val COUNTDOWN_TICK_MILLIS = 1_000

class PictureOfTheDayViewModel(
    private val getPictureOfTheDay: GetPictureOfTheDayInteractor,
    private val checkIfPictureIsFavourite: CheckIfPictureIsFavouriteInteractor,
    private val addFavouritePicture: AddFavouritePictureInteractor,
    private val deleteFavouritePicture: DeleteFavouritePictureInteractor,
    private val networkMonitor: NetworkMonitor,
    // Injectable so the countdown is deterministic in tests (the ticker formats from wall-clock time).
    private val clock: Clock = Clock.System
) : StateActionsViewModel<PictureOfTheDayUiState, Nothing>(PictureOfTheDayUiState()) {

    private var resumed = false
    private var countdownJob: Job? = null
    private var favouriteToggleJob: Job? = null
    private var loadJob: Job? = null

    init {
        observeConnectivity()
        observeFavouriteState()
    }

    private fun observeConnectivity() = launch {
        networkMonitor.isConnected.collect { connected ->
            // Fetch only until the picture is loaded; keeps parity with the original reconnect guard.
            if (currentState.screenState !is Success) {
                // Guard against a second connectivity emission starting a concurrent load whose result
                // would clobber the first one.
                if (connected) {
                    if (loadJob?.isActive != true) loadJob = launch { loadPictureOfTheDay() }
                } else {
                    updateState { copy(screenState = PictureOfTheDayScreenState.NoInternet) }
                }
            }
        }
    }

    private fun observeFavouriteState() = launch {
        checkIfPictureIsFavourite().collect { favourite ->
            updateState { copy(isFavourite = favourite) }
        }
    }

    fun handleUiEvent(event: PictureOfTheDayUiEvent) {
        when (event) {
            Resumed -> {
                resumed = true
                startCountdown()
            }

            Paused -> {
                resumed = false
                countdownJob?.cancel()
                countdownJob = null
            }

            FavouritePressed -> onFavouriteClicked()

            RetryPressed -> onRetryPressed()
        }
    }

    private fun onRetryPressed() {
        if (currentState.screenState is Success) return
        if (loadJob?.isActive == true) return
        loadJob = launch { loadPictureOfTheDay() }
    }

    private suspend fun loadPictureOfTheDay() {
        updateState { copy(screenState = PictureOfTheDayScreenState.Loading) }
        val newScreenState = when (val result = getPictureOfTheDay()) {
            is Result.Success -> Success(result.data)
            // Never surface the raw throwable message: Ktor embeds the full request URL in it, which
            // carries the API key as a query parameter.
            is Result.HttpError, is Result.Exception -> PictureOfTheDayScreenState.Error
        }
        updateState { copy(screenState = newScreenState) }
        if (newScreenState is Success) startCountdown()
    }

    // Live countdown to the next APOD (00:00 GMT-4). Runs only while the screen is resumed AND a
    // picture is loaded — mirrors the original app and keeps the ViewModel unit-testable (the ticker
    // never spins when tests do not send Resumed).
    private fun startCountdown() {
        if (!resumed) return
        if (currentState.screenState !is Success) return
        if (countdownJob?.isActive == true) return
        countdownJob = launch {
            while (isActive) {
                updateState { copy(timeToNewPicture = formatTimeToNextApod(clock.now())) }
                delay(COUNTDOWN_TICK_MILLIS.milliseconds)
            }
        }
    }

    private fun onFavouriteClicked() {
        val picture = (currentState.screenState as? Success)?.picture ?: return
        // isFavourite is a stale read of an async DB Flow, so ignore taps while a toggle is in flight —
        // otherwise a fast double tap inserts the same picture twice.
        if (favouriteToggleJob?.isActive == true) return
        favouriteToggleJob = launch {
            try {
                if (currentState.isFavourite) {
                    deleteFavouritePicture(picture.date)
                } else {
                    addFavouritePicture(picture)
                }
            } catch (_: Exception) {
                // Original app only logged the failure; the favourite Flow drives isFavourite.
            }
        }
    }
}
