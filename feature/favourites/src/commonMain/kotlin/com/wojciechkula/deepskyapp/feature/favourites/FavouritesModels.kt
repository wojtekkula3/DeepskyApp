package com.wojciechkula.deepskyapp.feature.favourites

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel

data class FavouritesUiState(val screenState: FavouritesScreenState = FavouritesScreenState.Loading)

sealed interface FavouritesScreenState {
    data class Success(val pictures: List<FavouritePictureModel>) : FavouritesScreenState
    data object Loading : FavouritesScreenState
    data object Empty : FavouritesScreenState
}

sealed interface FavouritesUiEvent {
    data class ItemPressed(val date: String) : FavouritesUiEvent
    data object AboutPressed : FavouritesUiEvent
}

sealed interface FavouritesUiAction {
    data class OpenDetails(val date: String) : FavouritesUiAction
    data object OpenAbout : FavouritesUiAction
}
