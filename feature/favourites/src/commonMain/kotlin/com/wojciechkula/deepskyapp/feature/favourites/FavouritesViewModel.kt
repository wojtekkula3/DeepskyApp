package com.wojciechkula.deepskyapp.feature.favourites

import com.wojciechkula.deepskyapp.core.mvvm.StateActionsViewModel
import com.wojciechkula.deepskyapp.domain.interactor.GetFavouritePicturesInteractor
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Empty
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Success
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiAction.OpenAbout
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiAction.OpenDetails
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiEvent.AboutPressed
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiEvent.ItemPressed

class FavouritesViewModel(
    private val getFavouritePictures: GetFavouritePicturesInteractor,
) : StateActionsViewModel<FavouritesUiState, FavouritesUiAction>(FavouritesUiState()) {

    init {
        observeFavouritePictures()
    }

    fun handleUiEvent(event: FavouritesUiEvent) {
        when (event) {
            is ItemPressed -> action(OpenDetails(event.date))
            AboutPressed -> action(OpenAbout)
        }
    }

    private fun observeFavouritePictures() = launch {
        getFavouritePictures().collect { pictures ->
            val screenState = if (pictures.isEmpty()) {
                Empty
            } else {
                Success(pictures.sortedByDescending { it.date })
            }
            updateState { copy(screenState = screenState) }
        }
    }
}
