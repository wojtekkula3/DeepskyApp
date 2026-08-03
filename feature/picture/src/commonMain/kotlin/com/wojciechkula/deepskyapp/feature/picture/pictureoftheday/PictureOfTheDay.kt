package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.feature.picture.LabelledText
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_add_to_favourites
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_copyright
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_date
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_explanation
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_load_error
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_new_picture_in
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_no_internet
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_not_available
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_try_again
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_remove_from_favourites
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Error
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Loading
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.NoInternet
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.FavouritePressed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Paused
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Resumed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.RetryPressed
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.painterResource
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
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (val screenState = uiState.screenState) {
                Error -> MessageContent(
                    message = stringResource(Res.string.picture_of_the_day_load_error),
                    onRetry = { uiEvent(RetryPressed) }
                )

                is Success -> SuccessContent(
                    picture = screenState.picture,
                    isFavourite = uiState.isFavourite,
                    timeToNewPicture = uiState.timeToNewPicture,
                    onFavouriteClick = { uiEvent(FavouritePressed) }
                )

                NoInternet -> MessageContent(
                    message = stringResource(Res.string.picture_of_the_day_no_internet),
                    onRetry = { uiEvent(RetryPressed) }
                )

                Loading -> CircularProgressIndicator(modifier = Modifier.padding(top = 64.dp))
            }
        }
    }
}

@Composable
private fun MessageContent(message: String, onRetry: () -> Unit) {
    Text(
        text = message,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.padding(top = 64.dp)
    )
    Button(
        onClick = onRetry,
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text(stringResource(Res.string.picture_of_the_day_try_again))
    }
}

@Composable
private fun SuccessContent(
    picture: PictureOfTheDayModel,
    isFavourite: Boolean,
    timeToNewPicture: String,
    onFavouriteClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        if (picture.mediaType == "image") {
            AsyncImage(
                model = picture.url,
                contentDescription = picture.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .zoomable(rememberZoomState())
            )
        } else {
            Text(
                text = stringResource(Res.string.picture_of_the_day_not_available),
                modifier = Modifier.padding(24.dp)
            )
        }
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
                Text(picture.title, style = MaterialTheme.typography.titleLarge)
                if (picture.mediaType == "image") {
                    IconButton(onClick = onFavouriteClick) {
                        Icon(
                            painter = painterResource(DesignSystemRes.drawable.ic_favourite),
                            contentDescription = if (isFavourite) {
                                stringResource(Res.string.picture_remove_from_favourites)
                            } else {
                                stringResource(Res.string.picture_add_to_favourites)
                            },
                            tint = if (isFavourite) {
                                MaterialTheme.colorScheme.error
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
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
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(12.dp)
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

@Preview
@Composable
private fun PictureOfTheDaySuccessPreview() {
    DeepskyTheme {
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
private fun PictureOfTheDayLoadingPreview() {
    DeepskyTheme {
        PictureOfTheDayScreen(uiState = PictureOfTheDayUiState(screenState = Loading), uiEvent = {})
    }
}

@Preview
@Composable
private fun PictureOfTheDayNoInternetPreview() {
    DeepskyTheme {
        PictureOfTheDayScreen(uiState = PictureOfTheDayUiState(screenState = NoInternet), uiEvent = {})
    }
}

@Preview
@Composable
private fun PictureOfTheDayErrorPreview() {
    DeepskyTheme {
        PictureOfTheDayScreen(uiState = PictureOfTheDayUiState(screenState = Error), uiEvent = {})
    }
}
