package com.wojciechkula.deepskyapp.presentation.pictureoftheday

import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel

data class PictureOfTheDayViewState(
    val pictureOfTheDay: PictureOfTheDayModel? = null,
    val hasInternetConnection: Boolean = false,
    val isLoading: Boolean = false
)