package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodDimensions
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.domain.model.MediaKind
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.model.mediaKind
import com.wojciechkula.deepskyapp.feature.picture.LabelledText
import com.wojciechkula.deepskyapp.feature.picture.PictureMedia
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Error
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Loading
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.NoInternet
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.ServerUnreachable
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.FavouritePressed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.MediaFailed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Paused
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Resumed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.RetryPressed
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_add_to_favourites
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_copyright
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_date
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_explanation
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_no_internet
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_load_error
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_new_picture_in
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_server_unreachable
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_try_again
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_remove_from_favourites
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun PictureOfTheDay(
    contentPadding: PaddingValues = PaddingValues(),
    viewModel: PictureOfTheDayViewModel = koinViewModel()
) {
    val uiState by viewModel.states.collectAsStateWithLifecycle()

    LifecycleResumeEffect(Unit) {
        viewModel.handleUiEvent(Resumed)
        onPauseOrDispose { viewModel.handleUiEvent(Paused) }
    }

    PictureOfTheDayScreen(
        uiState = uiState,
        uiEvent = viewModel::handleUiEvent,
        contentPadding = contentPadding
    )
}

@Composable
private fun PictureOfTheDayScreen(
    uiState: PictureOfTheDayUiState,
    uiEvent: (PictureOfTheDayUiEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    Scaffold { padding ->
        when (val screenState = uiState.screenState) {
            Error -> MessageContent(
                message = stringResource(Res.string.picture_of_the_day_load_error),
                onRetry = { uiEvent(RetryPressed) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )

            // Inside the scroll, so the content keeps drawing behind whatever floats over the bottom.
            is Success -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
                    .padding(contentPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                SuccessContent(
                    picture = screenState.picture,
                    isFavourite = uiState.isFavourite,
                    isOffline = uiState.isOffline,
                    timeToNewPicture = uiState.timeToNewPicture,
                    onFavouriteClick = { uiEvent(FavouritePressed) },
                    onMediaFailed = { uiEvent(MediaFailed) }
                )
            }

            NoInternet -> MessageContent(
                message = stringResource(Res.string.picture_no_internet),
                onRetry = { uiEvent(RetryPressed) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )

            ServerUnreachable -> MessageContent(
                message = stringResource(Res.string.picture_of_the_day_server_unreachable),
                onRetry = { uiEvent(RetryPressed) },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )

            Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun MessageContent(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = ApodTheme.typography.headlineSmall,
            textAlign = TextAlign.Center
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(stringResource(Res.string.picture_of_the_day_try_again))
        }
    }
}

@Composable
private fun SuccessContent(
    picture: PictureOfTheDayModel,
    isFavourite: Boolean,
    isOffline: Boolean,
    timeToNewPicture: String,
    onFavouriteClick: () -> Unit,
    onMediaFailed: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        PictureMedia(
            url = picture.url,
            mediaType = picture.mediaType,
            title = picture.title,
            thumbnailUrl = picture.thumbnailUrl,
            isOffline = isOffline,
            onMediaFailed = onMediaFailed
        )
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(picture.title, style = ApodTheme.typography.titleLarge)
                if (mediaKind(picture.mediaType, picture.url) != MediaKind.UNSUPPORTED) {
                    IconButton(onClick = onFavouriteClick) {
                        Icon(
                            painter = painterResource(DesignSystemRes.drawable.ic_favourite),
                            contentDescription = if (isFavourite) {
                                stringResource(Res.string.picture_remove_from_favourites)
                            } else {
                                stringResource(Res.string.picture_add_to_favourites)
                            },
                            tint = if (isFavourite) {
                                ApodTheme.colors.favorite
                            } else {
                                ApodTheme.colors.onSurfaceVariant
                            }
                        )
                    }
                }
            }
            picture.copyright?.let {
                LabelledText(
                    label = stringResource(Res.string.picture_label_copyright),
                    value = it.replace("\n", "")
                )
            }
            LabelledText(label = stringResource(Res.string.picture_label_date), value = picture.date)
            LabelledText(
                label = stringResource(Res.string.picture_label_explanation),
                value = picture.explanation
            )
        }
    }
    if (timeToNewPicture.isNotEmpty()) {
        Text(
            text = stringResource(Res.string.picture_of_the_day_new_picture_in, timeToNewPicture),
            modifier = Modifier.padding(
                top = ApodDimensions.marginSmallMedium,
                start = ApodDimensions.marginLarge,
                end = ApodDimensions.marginLarge
            ),
            style = ApodTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            color = ApodTheme.colors.onSurfaceVariant
        )
    }
}

private val previewPicture = PictureOfTheDayModel(
    copyright = "NASA",
    date = "2026-07-12",
    explanation = "A demo nebula rendered in the preview.",
    hdUrl = "",
    mediaType = "image",
    serviceVersion = "v1",
    title = "Preview Nebula",
    url = ""
)

// An empty url has no recognised extension, so this classifies as an embed with no thumbnail.
private val previewVideo = previewPicture.copy(
    mediaType = "video"
)

private val previewVideoFile = previewPicture.copy(
    mediaType = "video",
    url = "https://apod.nasa.gov/apod/image/2608/eso2612b.mp4",
    title = "Preview Clip"
)

@Preview
@Composable
private fun PictureOfTheDaySuccessPreview() {
    ApodTheme {
        PictureOfTheDayScreen(
            uiState = PictureOfTheDayUiState(
                screenState = Success(previewPicture),
                isFavourite = true,
                timeToNewPicture = "8h 0min 0s"
            ),
            uiEvent = {}
        )
    }
}

@Preview
@Composable
private fun PictureOfTheDayVideoNoThumbnailPreview() {
    ApodTheme {
        PictureOfTheDayScreen(
            uiState = PictureOfTheDayUiState(
                screenState = Success(previewVideo),
                isFavourite = true,
                timeToNewPicture = "8h 0min 0s"
            ),
            uiEvent = {}
        )
    }
}

// A direct video file: the card shows the player's stand-in and the button that opens it full screen.
@Preview
@Composable
private fun PictureOfTheDayVideoFilePreview() {
    ApodTheme {
        PictureOfTheDayScreen(
            uiState = PictureOfTheDayUiState(
                screenState = Success(previewVideoFile),
                isFavourite = false,
                timeToNewPicture = "3h 12min 5s"
            ),
            uiEvent = {}
        )
    }
}

@Preview
@Composable
private fun PictureOfTheDayLoadingPreview() {
    ApodTheme {
        PictureOfTheDayScreen(uiState = PictureOfTheDayUiState(screenState = Loading), uiEvent = {})
    }
}

@Preview
@Composable
private fun PictureOfTheDayNoInternetPreview() {
    ApodTheme {
        PictureOfTheDayScreen(uiState = PictureOfTheDayUiState(screenState = NoInternet), uiEvent = {})
    }
}

@Preview
@Composable
private fun PictureOfTheDayErrorPreview() {
    ApodTheme {
        PictureOfTheDayScreen(uiState = PictureOfTheDayUiState(screenState = Error), uiEvent = {})
    }
}

@Preview
@Composable
private fun PictureOfTheDayServerUnreachablePreview() {
    ApodTheme {
        PictureOfTheDayScreen(uiState = PictureOfTheDayUiState(screenState = ServerUnreachable), uiEvent = {})
    }
}
