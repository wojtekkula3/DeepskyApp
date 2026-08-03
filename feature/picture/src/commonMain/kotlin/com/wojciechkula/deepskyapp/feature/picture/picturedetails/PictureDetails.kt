package com.wojciechkula.deepskyapp.feature.picture.picturedetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.wojciechkula.deepskyapp.core.designsystem.component.TopSnackbarHost
import com.wojciechkula.deepskyapp.core.designsystem.component.TopSnackbarType
import com.wojciechkula.deepskyapp.core.designsystem.icon.DeepskyIcons
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.core.mvvm.ActionsEffect
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.feature.picture.LabelledText
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Loading
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.NotFound
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiAction.NavigateBack
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.BackPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.DeleteConfirmedPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.SnackbarDismissed
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
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
                    .verticalScroll(rememberScrollState())
            ) {
                IconButton(
                    onClick = { uiEvent(BackPressed) },
                    modifier = Modifier.padding(start = 16.dp, top = 8.dp)
                ) {
                    Icon(painter = DeepskyIcons.back(), contentDescription = "Back")
                }
                when (val screenState = uiState.screenState) {
                    Loading -> LoadingContent()

                    NotFound -> MessageContent("This picture is no longer in your favourites")

                    is Success -> SuccessContent(
                        picture = screenState.picture,
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
private fun Snackbar(
    modifier: Modifier = Modifier,
    message: String?,
    onDismiss: () -> Unit
) {
    message?.let {
        TopSnackbarHost(
            modifier = modifier,
            message = it,
            type = TopSnackbarType.ERROR,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun MessageContent(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 64.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(text = message, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun SuccessContent(
    picture: FavouritePictureModel,
    onDeleteConfirmed: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Picture(picture)
    DescriptionBox(picture)
    Button(
        onClick = { showDeleteDialog = true },
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        Text("Delete from favourite")
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Confirm to delete") },
            text = { Text("Are sure you want to delete this picture?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        onDeleteConfirmed()
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun Picture(picture: FavouritePictureModel) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp)
    ) {
        AsyncImage(
            model = picture.url,
            contentDescription = picture.title,
            modifier = Modifier
                .fillMaxWidth()
                .zoomable(rememberZoomState())
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
            Text(picture.title, style = MaterialTheme.typography.titleLarge)
            picture.copyright?.let { LabelledText(label = "Copyright:", value = it.replace("\n", "")) }
            LabelledText(label = "Date:", value = picture.date)
            LabelledText(label = "Explanation:", value = picture.explanation)
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
    DeepskyTheme {
        PictureDetailsScreen(
            uiState = PictureDetailsUiState(screenState = Success(previewPicture)),
            uiEvent = {}
        )
    }
}

@Preview
@Composable
private fun PictureDetailsLoadingPreview() {
    DeepskyTheme {
        PictureDetailsScreen(uiState = PictureDetailsUiState(screenState = Loading), uiEvent = {})
    }
}

@Preview
@Composable
private fun PictureDetailsNotFoundPreview() {
    DeepskyTheme {
        PictureDetailsScreen(uiState = PictureDetailsUiState(screenState = NotFound), uiEvent = {})
    }
}

@Preview
@Composable
private fun PictureDetailsSnackbarPreview() {
    DeepskyTheme {
        PictureDetailsScreen(
            uiState = PictureDetailsUiState(
                screenState = Success(previewPicture),
                snackbarMessage = "Error while deleting the picture"
            ),
            uiEvent = {}
        )
    }
}
