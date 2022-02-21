package com.wojciechkula.deepskyapp.presentation.pictureoftheday

sealed class PictureOfTheDayViewEvent {

    data class ShowError(val message: String) : PictureOfTheDayViewEvent()
}