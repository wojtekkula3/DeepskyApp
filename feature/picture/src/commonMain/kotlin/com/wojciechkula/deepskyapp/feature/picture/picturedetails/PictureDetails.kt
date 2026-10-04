package com.wojciechkula.deepskyapp.feature.picture.picturedetails

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wojciechkula.deepskyapp.core.designsystem.ApodScreenPreview
import com.wojciechkula.deepskyapp.core.designsystem.ScreenLightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.ApodScaffold
import com.wojciechkula.deepskyapp.core.designsystem.component.TopSnackbarHost
import com.wojciechkula.deepskyapp.core.designsystem.component.TopSnackbarType
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.core.mvvm.ActionsEffect
import com.wojciechkula.deepskyapp.core.mvvm.TrackScreen
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Loading
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.NotFound
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiAction.NavigateBack
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.BackPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.DeleteConfirmedPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.MediaOpenPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.SnackbarDismissed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.ui.LoadingScreen
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.ui.NotFoundScreen
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.ui.SuccessScreen
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.ui.previewPicture
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.ui.previewVideoFile
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_delete_error
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun PictureDetails(
    date: String,
    onBack: () -> Unit,
    viewModel: PictureDetailsViewModel = koinViewModel { parametersOf(date) }
) {
    val uiState by viewModel.states.collectAsStateWithLifecycle()
    TrackScreen(viewModel)

    ActionsEffect(viewModel.actions) { action ->
        when (action) {
            NavigateBack -> onBack()
        }
    }

    PictureDetailsScreen(
        uiState = uiState,
        uiEvent = viewModel::handleUiEvent
    )
}

@Composable
private fun PictureDetailsScreen(
    uiState: PictureDetailsUiState,
    uiEvent: (PictureDetailsUiEvent) -> Unit
) {
    ApodScaffold(contentEndPadding = ApodTheme.dimensions.margin2xLarge) {
        val onBackClick = { uiEvent(BackPressed) }

        when (val screenState = uiState.screenState) {
            is Success -> SuccessScreen(
                picture = screenState.picture,
                uiState = uiState,
                onBackClick = onBackClick,
                onDeleteConfirmed = { uiEvent(DeleteConfirmedPressed) },
                onMediaOpenClick = { media -> uiEvent(MediaOpenPressed(media)) },
                modifier = scrollableModifier
            )

            NotFound -> NotFoundScreen(
                onBackClick = onBackClick,
                modifier = scrollableModifier
            )

            Loading -> LoadingScreen(
                onBackClick = onBackClick,
                modifier = screenModifier
            )
        }

        Snackbar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(padding),
            message = uiState.snackbarMessage,
            onDismiss = { uiEvent(SnackbarDismissed) }
        )
    }
}

@Composable
private fun Snackbar(
    modifier: Modifier = Modifier,
    message: PictureDetailsMessage?,
    onDismiss: () -> Unit
) {
    message?.let {
        TopSnackbarHost(
            modifier = modifier,
            message = when (it) {
                PictureDetailsMessage.DeleteFailed -> stringResource(Res.string.picture_details_delete_error)
            },
            type = TopSnackbarType.ERROR,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun PictureDetailsPreview(uiState: PictureDetailsUiState) = ApodScreenPreview {
    PictureDetailsScreen(uiState = uiState, uiEvent = {})
}

@ScreenLightDarkPreview
@Composable
private fun PictureDetailsSuccessPreview() {
    PictureDetailsPreview(uiState = PictureDetailsUiState(screenState = Success(previewPicture)))
}

// A saved video file: the card shows the player's stand-in and the button that opens it full screen.
@ScreenLightDarkPreview
@Composable
private fun PictureDetailsVideoFilePreview() {
    PictureDetailsPreview(uiState = PictureDetailsUiState(screenState = Success(previewVideoFile)))
}

@ScreenLightDarkPreview
@Composable
private fun PictureDetailsOfflinePreview() {
    PictureDetailsPreview(uiState = PictureDetailsUiState(screenState = Success(previewPicture), isOffline = true))
}

@ScreenLightDarkPreview
@Composable
private fun PictureDetailsLoadingPreview() {
    PictureDetailsPreview(uiState = PictureDetailsUiState(screenState = Loading))
}

@ScreenLightDarkPreview
@Composable
private fun PictureDetailsNotFoundPreview() {
    PictureDetailsPreview(uiState = PictureDetailsUiState(screenState = NotFound))
}

@ScreenLightDarkPreview
@Composable
private fun PictureDetailsSnackbarPreview() {
    PictureDetailsPreview(
        uiState = PictureDetailsUiState(
            screenState = Success(previewPicture),
            snackbarMessage = PictureDetailsMessage.DeleteFailed
        )
    )
}
