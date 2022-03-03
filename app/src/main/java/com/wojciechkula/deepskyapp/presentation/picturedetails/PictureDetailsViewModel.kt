package com.wojciechkula.deepskyapp.presentation.picturedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hadilq.liveevent.LiveEvent
import com.wojciechkula.deepskyapp.domain.interactor.DeleteFavouritePictureInteractor
import com.wojciechkula.deepskyapp.presentation.favouritepictures.list.FavouritePicturesItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PictureDetailsViewModel @Inject constructor(
    private val deleteFavouritePictureInteractor: DeleteFavouritePictureInteractor
) : ViewModel() {

    private var _picture = MutableLiveData<FavouritePicturesItem>()
    val picture: LiveData<FavouritePicturesItem>
        get() = _picture

    private var _viewEvent = LiveEvent<PictureDetailsViewEvent>()
    val viewEvent: LiveData<PictureDetailsViewEvent>
        get() = _viewEvent


    fun initViews(pictureData: FavouritePicturesItem) {
        _picture.postValue(pictureData)
    }

    fun deleteFavouritePicture(date: String) {
        viewModelScope.launch {
            try {
                deleteFavouritePictureInteractor(date)
                _viewEvent.postValue(PictureDetailsViewEvent.ShowFavouritePictures)
            } catch (e: Exception) {
                Timber.e("Exception while deleting favourite picture: $e")
                _viewEvent.postValue(PictureDetailsViewEvent.Error(e.message.toString()))
            }
        }
    }
}