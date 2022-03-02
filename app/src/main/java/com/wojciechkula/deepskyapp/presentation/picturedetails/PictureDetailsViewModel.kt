package com.wojciechkula.deepskyapp.presentation.picturedetails

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.wojciechkula.deepskyapp.presentation.favouritepictures.list.FavouritePicturesItem
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PictureDetailsViewModel @Inject constructor() : ViewModel() {

    private var _picture = MutableLiveData<FavouritePicturesItem>()
    val picture : LiveData<FavouritePicturesItem>
    get() = _picture

    fun initViews(pictureData: FavouritePicturesItem) {
        _picture.postValue(pictureData)
    }
}