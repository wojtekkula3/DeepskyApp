package com.wojciechkula.deepskyapp.presentation.pictureoftheday

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.wojciechkula.deepskyapp.domain.interactor.GetPictureOfTheDayInteractor
import com.wojciechkula.deepskyapp.extension.newBuilder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PictureOfTheDayViewModel @Inject constructor(
    private val getPictureOfTheDayInteractor: GetPictureOfTheDayInteractor,
) : ViewModel() {

    private var _viewState = MutableLiveData<PictureOfTheDayViewState>()
    val viewState: LiveData<PictureOfTheDayViewState>
        get() = _viewState

    private var _viewEvent = LiveEvent<PictureOfTheDayViewEvent>()
    val viewEvent: LiveData<PictureOfTheDayViewEvent>
        get() = _viewEvent

    init {
        _viewState.value = PictureOfTheDayViewState()
    }

    private fun getAstronomyPictureOfTheDay() {
        viewModelScope.launch {
            _viewState.value = viewState.newBuilder { copy(isLoading = true) }
            val response = getPictureOfTheDayInteractor()
            if (response.isSuccessful) {
                val apod = response.body()
                if (apod != null) {
                    _viewState.value = viewState.newBuilder { copy(pictureOfTheDay = apod, isLoading = false) }
                }
            } else {
                Log.d("APOD Api Error", response.message())
                _viewEvent.postValue(PictureOfTheDayViewEvent.ShowError(response.message()))
            }
        }
    }

    fun changeNetworkConnection(hasNetworkConnection: Boolean) {
        if (viewState.value?.pictureOfTheDay == null) {
            _viewState.value = viewState.newBuilder { copy(hasInternetConnection = hasNetworkConnection) }
            if (hasNetworkConnection) {
                getAstronomyPictureOfTheDay()
            }
        }
    }
}