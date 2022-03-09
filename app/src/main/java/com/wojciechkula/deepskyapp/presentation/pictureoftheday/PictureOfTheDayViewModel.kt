package com.wojciechkula.deepskyapp.presentation.pictureoftheday

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.wojciechkula.deepskyapp.domain.interactor.AddFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.CheckIfPictureIsFavouriteInteractor
import com.wojciechkula.deepskyapp.domain.interactor.DeleteFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetPictureOfTheDayInteractor
import com.wojciechkula.deepskyapp.extension.newBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject

@HiltViewModel
class PictureOfTheDayViewModel @Inject constructor(
    private val getPictureOfTheDayInteractor: GetPictureOfTheDayInteractor,
    private val checkIfPictureIsFavouriteInteractor: CheckIfPictureIsFavouriteInteractor,
    private val addFavouritePictureInteractor: AddFavouritePictureInteractor,
    private val deleteFavouritePictureInteractor: DeleteFavouritePictureInteractor
) : ViewModel() {

    private var _viewState = MutableLiveData<PictureOfTheDayViewState>()
    val viewState: LiveData<PictureOfTheDayViewState>
        get() = _viewState

    private var _viewEvent = LiveEvent<PictureOfTheDayViewEvent>()
    val viewEvent: LiveData<PictureOfTheDayViewEvent>
        get() = _viewEvent

    val isFavouriteState: LiveData<Boolean> = checkIfPictureIsFavouriteInteractor()

    init {
        _viewState.value = PictureOfTheDayViewState()
    }

    fun changeNetworkConnectionStatus(hasNetworkConnection: Boolean) {
        if (viewState.value?.pictureOfTheDay == null) {
            _viewState.value = viewState.newBuilder { copy(hasInternetConnection = hasNetworkConnection) }
            if (hasNetworkConnection) {
                getAstronomyPictureOfTheDay()
            }
        }
    }

    private fun getAstronomyPictureOfTheDay() {
        viewModelScope.launch {
            _viewState.value = viewState.newBuilder { copy(isLoading = true) }
            val response = getPictureOfTheDayInteractor()
            if (response.isSuccessful) {
                val apod = response.body()
                if (apod != null) {
                    _viewState.value = viewState.newBuilder {
                        copy(
                            pictureOfTheDay = apod,
                            isLoading = false
                        )
                    }
                    startCountdownToNextPicture()
                }
            } else {
                Timber.e("Error while getting response from APOD API", response.message())
                _viewEvent.postValue(PictureOfTheDayViewEvent.ShowError(response.message()))
            }
        }
    }

    private fun startCountdownToNextPicture() {
        viewModelScope.launch {
            val timeOfNewPictureUpdate = getDateOfNewPictureUpdate()
            while (true) {
                val currentDate = Calendar.getInstance()
                val diff = timeOfNewPictureUpdate.timeInMillis - currentDate.timeInMillis

                val hours = diff / (1000 * 60 * 60) % 24
                val minutes = diff / (1000 * 60) % 60
                val seconds = (diff / 1000) % 60
                _viewState.value =
                    viewState.newBuilder { copy(timeToNewPicture = "${hours}h ${minutes}min ${seconds}s") }
                delay(1000)
            }
        }
    }

    private fun getDateOfNewPictureUpdate(): Calendar {
        val date = Date()
        val eventDate = Calendar.getInstance()
        eventDate.timeZone = TimeZone.getTimeZone("GMT-04:00")
        eventDate.time = date
        eventDate.add(Calendar.DATE, 1)
        eventDate.set(Calendar.HOUR_OF_DAY, 0)
        eventDate.set(Calendar.MINUTE, 0)
        eventDate.set(Calendar.SECOND, 0)
        return eventDate
    }

    fun onFavouriteButtonClick(pictureBitmap: Bitmap) {
        viewModelScope.launch {
            val apod = viewState.value?.pictureOfTheDay
            if (apod != null) {
                if (isFavouriteState.value == true) {
                    try {
                        deleteFavouritePictureInteractor(apod.date)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                } else {
                    try {
                        addFavouritePictureInteractor.invoke(apod, pictureBitmap)
                    } catch (e: Exception) {
                        Timber.e(e)
                    }
                }
            }
        }
    }
}