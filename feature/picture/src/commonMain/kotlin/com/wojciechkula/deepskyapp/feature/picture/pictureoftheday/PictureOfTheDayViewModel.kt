package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.core.common.NetworkMonitor
import com.wojciechkula.deepskyapp.core.mvvm.StateActionsViewModel
import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.interactor.AddFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.CheckIfPictureIsFavouriteInteractor
import com.wojciechkula.deepskyapp.domain.interactor.DeleteFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetPictureOfTheDayInteractor
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.FavouritePressed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.MediaFailed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Paused
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Resumed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.RetryPressed
import kotlin.time.Clock
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Instant
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive

private const val COUNTDOWN_TICK_MILLIS = 1_000
private val REFRESH_RETRY_INTERVAL = 1.minutes
private val SERVER_ERROR_CODES = 500..599

@OptIn(ExperimentalCoroutinesApi::class)
class PictureOfTheDayViewModel(
    private val getPictureOfTheDay: GetPictureOfTheDayInteractor,
    private val checkIfPictureIsFavourite: CheckIfPictureIsFavouriteInteractor,
    private val addFavouritePicture: AddFavouritePictureInteractor,
    private val deleteFavouritePicture: DeleteFavouritePictureInteractor,
    private val networkMonitor: NetworkMonitor,
    private val clock: Clock,
    private val dateFormatter: DateFormatter
) : StateActionsViewModel<PictureOfTheDayUiState, Nothing>(PictureOfTheDayUiState()) {

    private var resumed = false
    private var countdownJob: Job? = null
    private var favouriteToggleJob: Job? = null
    private var loadJob: Job? = null
    private var lastRefreshAttempt: Instant? = null

    init {
        observeConnectivity()
        observeFavouriteState()
    }

    private fun observeConnectivity() = launch {
        networkMonitor.isConnected.collect { connected ->
            updateState { copy(isOffline = !connected) }
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

    // Keyed on the loaded picture's date, not on "today": the two differ once the APOD midnight passes.
    private fun observeFavouriteState() = launch {
        states
            .map { (it.screenState as? Success)?.picture?.date }
            .distinctUntilChanged()
            .flatMapLatest { date -> date?.let(checkIfPictureIsFavourite::invoke) ?: flowOf(false) }
            .collect { favourite -> updateState { copy(isFavourite = favourite) } }
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

            MediaFailed -> onMediaFailed()

            RetryPressed -> onRetryPressed()
        }
    }

    // Offline, the text around a picture is worth little without the picture, so the whole screen becomes
    // the connectivity error and its existing retry re-fetches. Connected, a failed media load is the
    // media's own problem and the slot reports it in place.
    private fun onMediaFailed() {
        if (!currentState.isOffline) return
        updateState { copy(screenState = PictureOfTheDayScreenState.NoInternet) }
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

            // Never surface the raw throwable message: Ktor embeds the full request URL in it.
            is Result.HttpError if result.code in SERVER_ERROR_CODES -> PictureOfTheDayScreenState.ServerError

            Result.ServerNotResponding -> PictureOfTheDayScreenState.ServerError

            is Result.HttpError, is Result.Exception -> PictureOfTheDayScreenState.Error

            Result.NetworkError -> PictureOfTheDayScreenState.ServerUnreachable
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
                val now = clock.now()
                updateState { copy(timeToNewPicture = formatTimeToNextApod(now)) }
                refreshIfOutdated(now)
                delay(COUNTDOWN_TICK_MILLIS.milliseconds)
            }
        }
    }

    // Keeps the old picture on screen: NASA may publish the new one late, and until then the API rejects
    // the new date, which would otherwise turn a working screen into an error at midnight.
    private fun refreshIfOutdated(now: Instant) {
        val picture = (currentState.screenState as? Success)?.picture ?: return
        if (picture.date == dateFormatter.currentApodDate()) return
        if (loadJob?.isActive == true) return
        lastRefreshAttempt?.let { if (now - it < REFRESH_RETRY_INTERVAL) return }
        lastRefreshAttempt = now
        loadJob = launch {
            val result = getPictureOfTheDay()
            if (result is Result.Success) updateState { copy(screenState = Success(result.data)) }
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
