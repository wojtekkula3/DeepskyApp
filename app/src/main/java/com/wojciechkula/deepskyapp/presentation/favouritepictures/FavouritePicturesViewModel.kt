package com.wojciechkula.deepskyapp.presentation.favouritepictures

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wojciechkula.deepskyapp.domain.interactor.GetFavouritePicturesInteractor
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavouritePicturesViewModel @Inject constructor(
    private val getFavouritePicturesInteractor: GetFavouritePicturesInteractor
) : ViewModel() {

    val picturesList : LiveData<List<PictureOfTheDay>> = getFavouritePicturesInteractor()

    private var _viewState = MutableLiveData<FavouritePicturesViewState>()
    val viewState : LiveData<FavouritePicturesViewState>
        get() = _viewState

}