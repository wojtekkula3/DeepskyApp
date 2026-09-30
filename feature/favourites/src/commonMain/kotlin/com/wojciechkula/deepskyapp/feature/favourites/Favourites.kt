package com.wojciechkula.deepskyapp.feature.favourites

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wojciechkula.deepskyapp.core.designsystem.ApodScreenPreview
import com.wojciechkula.deepskyapp.core.designsystem.ScreenLightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.ApodScaffold
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.core.mvvm.ActionsEffect
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Empty
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Loading
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Success
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiAction.OpenAbout
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiAction.OpenDetails
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiEvent.AboutPressed
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiEvent.ItemPressed
import com.wojciechkula.deepskyapp.feature.favourites.ui.EmptyScreen
import com.wojciechkula.deepskyapp.feature.favourites.ui.LoadingScreen
import com.wojciechkula.deepskyapp.feature.favourites.ui.SuccessScreen
import com.wojciechkula.deepskyapp.feature.favourites.ui.previewImage
import com.wojciechkula.deepskyapp.feature.favourites.ui.previewPictures
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun Favourites(
    onOpenDetails: (date: String) -> Unit,
    onOpenAbout: () -> Unit,
    isEntering: Boolean = false,
    viewModel: FavouritesViewModel = koinViewModel()
) {
    val uiState by viewModel.states.collectAsStateWithLifecycle()

    ActionsEffect(viewModel.actions) { action ->
        when (action) {
            is OpenDetails -> onOpenDetails(action.date)
            OpenAbout -> onOpenAbout()
        }
    }

    // The grid's first composition takes a few frames on a mid-range phone, so a list that arrives while
    // the screen is still sliding in waits for the slide to end instead of stalling it.
    var holdLoading by remember { mutableStateOf(uiState.screenState == Loading) }
    LaunchedEffect(isEntering) {
        if (!isEntering) holdLoading = false
    }

    FavouritesScreen(
        uiState = if (holdLoading) uiState.copy(screenState = Loading) else uiState,
        uiEvent = viewModel::handleUiEvent
    )
}

@Composable
private fun FavouritesScreen(
    uiState: FavouritesUiState,
    uiEvent: (FavouritesUiEvent) -> Unit
) {
    ApodScaffold(contentEndPadding = ApodTheme.dimensions.marginMedium) {
        val onAboutClick = { uiEvent(AboutPressed) }

        when (val screenState = uiState.screenState) {
            is Success -> SuccessScreen(
                pictures = screenState.pictures,
                onAboutClick = onAboutClick,
                onPictureClick = { date -> uiEvent(ItemPressed(date)) },
                modifier = lazyModifier,
                contentPadding = lazyContentPadding
            )

            Empty -> EmptyScreen(
                onAboutClick = onAboutClick,
                modifier = screenModifier
            )

            Loading -> LoadingScreen(
                onAboutClick = onAboutClick,
                modifier = screenModifier
            )
        }
    }
}

@Composable
private fun FavouritesPreview(uiState: FavouritesUiState) = ApodScreenPreview {
    FavouritesScreen(uiState = uiState, uiEvent = {})
}

@ScreenLightDarkPreview
@Composable
private fun FavouritesSuccessPreview() {
    FavouritesPreview(uiState = FavouritesUiState(screenState = Success(listOf(previewImage))))
}

// The mixed grid is the interesting one: only here do the play badge and the reserved video tile show
// up next to a picture tile that takes its height from the image.
@ScreenLightDarkPreview
@Composable
private fun FavouritesMixedMediaPreview() {
    FavouritesPreview(uiState = FavouritesUiState(screenState = Success(previewPictures)))
}

@ScreenLightDarkPreview
@Composable
private fun FavouritesEmptyPreview() {
    FavouritesPreview(uiState = FavouritesUiState(screenState = Empty))
}

@ScreenLightDarkPreview
@Composable
private fun FavouritesLoadingPreview() {
    FavouritesPreview(uiState = FavouritesUiState(screenState = Loading))
}
