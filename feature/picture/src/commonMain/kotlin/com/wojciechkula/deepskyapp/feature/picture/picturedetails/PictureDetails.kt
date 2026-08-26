package com.wojciechkula.deepskyapp.feature.picture.picturedetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.wojciechkula.deepskyapp.core.designsystem.component.TopSnackbarHost
import com.wojciechkula.deepskyapp.core.designsystem.component.TopSnackbarType
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.content_description_back
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_back
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.core.mvvm.ActionsEffect
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.feature.picture.LabelledText
import com.wojciechkula.deepskyapp.feature.picture.PictureMedia
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Loading
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.NotFound
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiAction.NavigateBack
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.BackPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.DeleteConfirmedPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.SnackbarDismissed
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_delete
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_delete_error
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_dialog_cancel
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_dialog_confirm
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_dialog_message
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_dialog_title
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_not_found
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_copyright
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_date
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_explanation
import org.jetbrains.compose.resources.painterResource
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
    Scaffold { padding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            ) {
                BackButton(uiEvent)
                when (val screenState = uiState.screenState) {
                    Loading -> LoadingContent(modifier = Modifier.weight(1f))
                    NotFound -> MessageContent(
                        message = stringResource(Res.string.picture_details_not_found),
                        modifier = Modifier.weight(1f)
                    )

                    is Success -> SuccessScreen(
                        picture = screenState.picture,
                        isOffline = uiState.isOffline,
                        onDeleteConfirmed = { uiEvent(DeleteConfirmedPressed) }
                    )
                }
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
}

@Composable
private fun BackButton(uiEvent: (PictureDetailsUiEvent) -> Unit) {
    IconButton(
        onClick = { uiEvent(BackPressed) },
        modifier = Modifier.padding(start = 16.dp, top = 8.dp)
    ) {
        Icon(
            painter = painterResource(DesignSystemRes.drawable.ic_back),
            contentDescription = stringResource(DesignSystemRes.string.content_description_back)
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
private fun LoadingContent(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageContent(
    message: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message,
            style = ApodTheme.typography.titleMedium,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun ColumnScope.SuccessScreen(
    picture: FavouritePictureModel,
    isOffline: Boolean,
    onDeleteConfirmed: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState())
    ) {
        Picture(picture = picture, isOffline = isOffline)
        DescriptionBox(picture)
        Button(
            onClick = { showDeleteDialog = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(stringResource(Res.string.picture_details_delete))
        }

        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text(stringResource(Res.string.picture_details_dialog_title)) },
                text = { Text(stringResource(Res.string.picture_details_dialog_message)) },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            onDeleteConfirmed()
                        }
                    ) {
                        Text(stringResource(Res.string.picture_details_dialog_confirm))
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDeleteDialog = false }) {
                        Text(stringResource(Res.string.picture_details_dialog_cancel))
                    }
                }
            )
        }
    }
}

@Composable
private fun Picture(
    picture: FavouritePictureModel,
    isOffline: Boolean
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, top = 12.dp, bottom = 24.dp)
    ) {
        // No onMediaFailed here on purpose: a favourite's text comes from the database and stays
        // readable offline, so only the media slot reports the failure.
        PictureMedia(
            url = picture.url,
            mediaType = picture.mediaType,
            title = picture.title,
            thumbnailUrl = picture.thumbnailUrl,
            isOffline = isOffline
        )
    }
}

@Composable
private fun DescriptionBox(picture: FavouritePictureModel) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(picture.title, style = ApodTheme.typography.titleLarge)
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
}

private val previewPicture = FavouritePictureModel(
    id = 1L,
    copyright = "NASA",
    date = "2026-07-10",
    explanation = "A saved favourite rendered in the preview.",
    hdUrl = "",
    mediaType = "image",
    serviceVersion = "v1",
    title = "Preview Favourite",
    url = ""
)

@Preview
@Composable
private fun PictureDetailsSuccessPreview() {
    ApodTheme {
        PictureDetailsScreen(
            uiState = PictureDetailsUiState(screenState = Success(previewPicture)),
            uiEvent = {}
        )
    }
}

private val previewVideoFile = previewPicture.copy(
    mediaType = "video",
    url = "https://apod.nasa.gov/apod/image/2608/eso2612b.mp4",
    title = "Preview Clip"
)

// A saved video file: the card shows the player's stand-in and the button that opens it full screen.
@Preview
@Composable
private fun PictureDetailsVideoFilePreview() {
    ApodTheme {
        PictureDetailsScreen(
            uiState = PictureDetailsUiState(screenState = Success(previewVideoFile)),
            uiEvent = {}
        )
    }
}

@Preview
@Composable
private fun PictureDetailsLoadingPreview() {
    ApodTheme {
        PictureDetailsScreen(
            uiState = PictureDetailsUiState(screenState = Loading),
            uiEvent = {}
        )
    }
}

@Preview
@Composable
private fun PictureDetailsNotFoundPreview() {
    ApodTheme {
        PictureDetailsScreen(
            uiState = PictureDetailsUiState(screenState = NotFound),
            uiEvent = {}
        )
    }
}

@Preview
@Composable
private fun PictureDetailsSnackbarPreview() {
    ApodTheme {
        PictureDetailsScreen(
            uiState = PictureDetailsUiState(
                screenState = Success(previewPicture),
                snackbarMessage = PictureDetailsMessage.DeleteFailed
            ),
            uiEvent = {}
        )
    }
}
