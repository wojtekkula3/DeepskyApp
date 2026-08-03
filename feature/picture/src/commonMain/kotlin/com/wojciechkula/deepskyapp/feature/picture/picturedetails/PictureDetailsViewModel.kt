package com.wojciechkula.deepskyapp.feature.picture.picturedetails

import com.wojciechkula.deepskyapp.core.mvvm.StateActionsViewModel
import com.wojciechkula.deepskyapp.domain.interactor.DeleteFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetFavouritePicturesInteractor
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.NotFound
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiAction.NavigateBack
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.BackPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.DeleteConfirmedPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.SnackbarDismissed
import kotlinx.coroutines.flow.map

class PictureDetailsViewModel(
    private val date: String,
    private val getFavouritePictures: GetFavouritePicturesInteractor,
    private val deleteFavouritePicture: DeleteFavouritePictureInteractor,
) : StateActionsViewModel<PictureDetailsUiState, PictureDetailsUiAction>(PictureDetailsUiState()) {

    init {
        observeFavouritePicture()
    }

    fun handleUiEvent(event: PictureDetailsUiEvent) {
        when (event) {
            BackPressed -> action(NavigateBack)
            DeleteConfirmedPressed -> onDeletePressed()
            SnackbarDismissed -> updateState { copy(snackbarMessage = null) }
        }
    }

    private fun observeFavouritePicture() = launch {
        getFavouritePictures()
            .map { pictures -> pictures.firstOrNull { it.date == date } }
            .collect { match ->
                when {
                    match != null -> updateState { copy(screenState = Success(match)) }
                    // Keep the last Success once we have one: the picture disappears from the list right
                    // after a delete, and blanking the screen before NavigateBack lands would flash.
                    // Before any Success, no match means the date simply is not a favourite (for example
                    // a restored back stack entry pointing at a deleted picture).
                    currentState.screenState !is Success -> updateState { copy(screenState = NotFound) }
                }
            }
    }

    private fun onDeletePressed() = launch {
        try {
            deleteFavouritePicture(date)
            action(NavigateBack)
        } catch (exception: Exception) {
            updateState { copy(snackbarMessage = PictureDetailsMessage.DeleteFailed) }
        }
    }
}
