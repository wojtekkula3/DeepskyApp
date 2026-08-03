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
import com.wojciechkula.deepskyapp.core.designsystem.icon.DeepskyIcons
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.feature.picture.LabelledText
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
                    message = "Could not load today's picture",
                    onRetry = { uiEvent(RetryPressed) }
                )

                is Success -> SuccessContent(
                    picture = screenState.picture,
                    isFavourite = uiState.isFavourite,
                    timeToNewPicture = uiState.timeToNewPicture,
                    onFavouriteClick = { uiEvent(FavouritePressed) }
                )

                NoInternet -> MessageContent(
                    message = "No internet connection",
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
        Text("Try again")
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
                text = "Picture is not available today",
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
                            painter = DeepskyIcons.favourite(),
                            contentDescription = if (isFavourite) {
                                "Remove from favourites"
                            } else {
                                "Add to favourites"
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
            picture.copyright?.let { LabelledText(label = "Copyright:", value = it.replace("\n", "")) }
            LabelledText(label = "Date:", value = picture.date)
            LabelledText(label = "Explanation:", value = picture.explanation)
        }
    }
    if (timeToNewPicture.isNotEmpty()) {
        Text(
            text = "New picture in: $timeToNewPicture",
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
