package com.wojciechkula.deepskyapp.presentation.picturedetails

sealed class PictureDetailsViewEvent {
    object ShowFavouritePictures : PictureDetailsViewEvent()
    data class Error(val message: String): PictureDetailsViewEvent()

}