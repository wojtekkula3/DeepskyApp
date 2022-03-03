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
import kotlinx.coroutines.launch
import timber.log.Timber
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
                }
            } else {
                Timber.e("Error while getting response from APOD API", response.message())
                _viewEvent.postValue(PictureOfTheDayViewEvent.ShowError(response.message()))
            }
        }
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