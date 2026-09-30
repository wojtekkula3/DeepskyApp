package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wojciechkula.deepskyapp.core.designsystem.ApodScreenPreview
import com.wojciechkula.deepskyapp.core.designsystem.ScreenLightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.ApodScaffold
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Error
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Loading
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.NoInternet
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.ServerError
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.ServerUnreachable
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.FavouritePressed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.MediaFailed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Paused
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Resumed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.RetryPressed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui.ErrorScreen
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui.LoadingScreen
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui.SuccessScreen
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui.previewPicture
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui.previewVideo
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui.previewVideoFile
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_no_internet
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_generic_error
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_server_error
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_server_unreachable
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PictureOfTheDay(
    viewModel: PictureOfTheDayViewModel = koinViewModel()
) {
    val uiState by viewModel.states.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.handleUiEvent(Resumed)
        onPauseOrDispose { viewModel.handleUiEvent(Paused) }
    }

    PictureOfTheDayScreen(
        uiState = uiState,
        uiEvent = viewModel::handleUiEvent
    )
}

@Composable
private fun PictureOfTheDayScreen(
    uiState: PictureOfTheDayUiState,
    uiEvent: (PictureOfTheDayUiEvent) -> Unit
) {
    ApodScaffold {
        when (val screenState = uiState.screenState) {
            is Success -> SuccessScreen(
                picture = screenState.picture,
                uiState = uiState,
                onFavouriteClick = { uiEvent(FavouritePressed) },
                onMediaFailed = { uiEvent(MediaFailed) },
                modifier = scrollableModifier
            )

            Error -> ErrorScreen(
                message = stringResource(Res.string.picture_of_the_day_generic_error),
                onRetry = { uiEvent(RetryPressed) },
                modifier = scrollableModifier
            )

            NoInternet -> ErrorScreen(
                message = stringResource(Res.string.picture_no_internet),
                onRetry = { uiEvent(RetryPressed) },
                modifier = scrollableModifier
            )

            ServerError -> ErrorScreen(
                message = stringResource(Res.string.picture_of_the_day_server_error),
                onRetry = { uiEvent(RetryPressed) },
                modifier = scrollableModifier
            )

            ServerUnreachable -> ErrorScreen(
                message = stringResource(Res.string.picture_of_the_day_server_unreachable),
                onRetry = { uiEvent(RetryPressed) },
                modifier = scrollableModifier
            )

            Loading -> LoadingScreen(modifier = screenModifier)
        }
    }
}

private val previewSuccessState = PictureOfTheDayUiState(
    screenState = Success(previewPicture),
    isFavourite = true,
    timeToNewPicture = "8h 0min 0s"
)

@Composable
private fun PictureOfTheDayPreview(uiState: PictureOfTheDayUiState) = ApodScreenPreview {
    PictureOfTheDayScreen(uiState = uiState, uiEvent = {})
}

@ScreenLightDarkPreview
@Composable
private fun PictureOfTheDaySuccessPreview() {
    PictureOfTheDayPreview(uiState = previewSuccessState)
}

@ScreenLightDarkPreview
@Composable
private fun PictureOfTheDayNotFavouritePreview() {
    PictureOfTheDayPreview(uiState = previewSuccessState.copy(isFavourite = false))
}

@ScreenLightDarkPreview
@Composable
private fun PictureOfTheDayVideoNoThumbnailPreview() {
    PictureOfTheDayPreview(uiState = previewSuccessState.copy(screenState = Success(previewVideo)))
}

// A direct video file: the card shows the player's stand-in and the button that opens it full screen.
@ScreenLightDarkPreview
@Composable
private fun PictureOfTheDayVideoFilePreview() {
    PictureOfTheDayPreview(
        uiState = PictureOfTheDayUiState(
            screenState = Success(previewVideoFile),
            isFavourite = false,
            timeToNewPicture = "3h 12min 5s"
        )
    )
}

@ScreenLightDarkPreview
@Composable
private fun PictureOfTheDayLoadingPreview() {
    PictureOfTheDayPreview(uiState = PictureOfTheDayUiState(screenState = Loading))
}

@ScreenLightDarkPreview
@Composable
private fun PictureOfTheDayNoInternetPreview() {
    PictureOfTheDayPreview(uiState = PictureOfTheDayUiState(screenState = NoInternet))
}

@ScreenLightDarkPreview
@Composable
private fun PictureOfTheDayErrorPreview() {
    PictureOfTheDayPreview(uiState = PictureOfTheDayUiState(screenState = Error))
}

@ScreenLightDarkPreview
@Composable
private fun PictureOfTheDayServerErrorPreview() {
    PictureOfTheDayPreview(uiState = PictureOfTheDayUiState(screenState = ServerError))
}

@ScreenLightDarkPreview
@Composable
private fun PictureOfTheDayServerUnreachablePreview() {
    PictureOfTheDayPreview(uiState = PictureOfTheDayUiState(screenState = ServerUnreachable))
}
