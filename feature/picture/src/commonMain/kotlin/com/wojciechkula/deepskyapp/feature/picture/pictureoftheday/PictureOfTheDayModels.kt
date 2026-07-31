package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel

data class PictureOfTheDayUiState(
    val screenState: PictureOfTheDayScreenState = PictureOfTheDayScreenState.Loading,
    val isFavourite: Boolean = false,
    val timeToNewPicture: String = ""
)

sealed interface PictureOfTheDayScreenState {
    data object Error : PictureOfTheDayScreenState
    data class Success(val picture: PictureOfTheDayModel) : PictureOfTheDayScreenState
    data object Loading : PictureOfTheDayScreenState
    data object NoInternet : PictureOfTheDayScreenState
}

sealed interface PictureOfTheDayUiEvent {
    data object FavouritePressed : PictureOfTheDayUiEvent
    data object Paused : PictureOfTheDayUiEvent
    data object Resumed : PictureOfTheDayUiEvent
    data object RetryPressed : PictureOfTheDayUiEvent
}
