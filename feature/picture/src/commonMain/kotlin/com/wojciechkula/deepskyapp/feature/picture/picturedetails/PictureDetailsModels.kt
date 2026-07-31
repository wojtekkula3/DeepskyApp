package com.wojciechkula.deepskyapp.feature.picture.picturedetails

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel

data class PictureDetailsUiState(
    val screenState: PictureDetailsScreenState = PictureDetailsScreenState.Loading,
    val snackbarMessage: String? = null
)

sealed interface PictureDetailsScreenState {
    data class Success(val picture: FavouritePictureModel) : PictureDetailsScreenState
    data object Loading : PictureDetailsScreenState
    data object NotFound : PictureDetailsScreenState
}

sealed interface PictureDetailsUiEvent {
    data object BackPressed : PictureDetailsUiEvent
    data object DeleteConfirmedPressed : PictureDetailsUiEvent
    data object SnackbarDismissed : PictureDetailsUiEvent
}

sealed interface PictureDetailsUiAction {
    data object NavigateBack : PictureDetailsUiAction
}
