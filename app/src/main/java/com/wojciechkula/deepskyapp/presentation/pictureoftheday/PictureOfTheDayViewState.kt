package com.wojciechkula.deepskyapp.presentation.pictureoftheday

import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay

data class PictureOfTheDayViewState(
    val pictureOfTheDay: PictureOfTheDay? = null,
    val hasInternetConnection: Boolean = false,
    val isLoading: Boolean = false
)