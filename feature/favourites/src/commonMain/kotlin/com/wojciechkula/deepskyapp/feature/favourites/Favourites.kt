package com.wojciechkula.deepskyapp.feature.favourites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_info
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_play
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.core.mvvm.ActionsEffect
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.MediaKind
import com.wojciechkula.deepskyapp.domain.model.mediaKind
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Empty
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Loading
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Success
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiAction.OpenAbout
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiAction.OpenDetails
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiEvent.AboutPressed
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiEvent.ItemPressed
import com.wojciechkula.deepskyapp.feature.favourites.resources.Res
import com.wojciechkula.deepskyapp.feature.favourites.resources.favourites_content_description_about
import com.wojciechkula.deepskyapp.feature.favourites.resources.favourites_empty
import com.wojciechkula.deepskyapp.feature.favourites.resources.favourites_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

private val PlayBadgeSize = 40.dp
private const val VIDEO_TILE_ASPECT_RATIO = 16f / 9f

@Composable
fun Favourites(
    onOpenDetails: (date: String) -> Unit,
    onOpenAbout: () -> Unit,
    contentPadding: PaddingValues = PaddingValues(),
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
        uiEvent = viewModel::handleUiEvent,
        contentPadding = contentPadding
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavouritesScreen(
    uiState: FavouritesUiState,
    uiEvent: (FavouritesUiEvent) -> Unit,
    contentPadding: PaddingValues = PaddingValues()
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(Res.string.favourites_title),
                        modifier = Modifier.padding(start = 8.dp)
                    )
                },
                actions = {
                    // The original app rendered this as a menu item with ic_info, not as a text button.
                    IconButton(onClick = { uiEvent(AboutPressed) }) {
                        Icon(
                            painter = painterResource(DesignSystemRes.drawable.ic_info),
                            contentDescription = stringResource(
                                Res.string.favourites_content_description_about
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = ApodTheme.colors.background
                )
            )
        }
    ) { padding ->
        when (val screenState = uiState.screenState) {
            Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            Empty -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text(stringResource(Res.string.favourites_empty))
            }

            is Success -> LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 24.dp),
                contentPadding = contentPadding,
                verticalItemSpacing = 8.dp,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(screenState.pictures, key = { it.date }) { picture ->
                    FavouriteCard(
                        picture = picture,
                        onClick = { uiEvent(ItemPressed(picture.date)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FavouriteCard(
    modifier: Modifier = Modifier,
    picture: FavouritePictureModel,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column {
            FavouriteThumbnail(picture = picture)
            Text(
                text = picture.title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = ApodTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            Text(
                text = picture.date,
                textAlign = TextAlign.Center,
                style = ApodTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }
    }
}

@Composable
private fun FavouriteThumbnail(picture: FavouritePictureModel) {
    val kind = mediaKind(picture.mediaType, picture.url)
    // An mp4 resolves through the video-frame fetcher on the singleton loader; an embed URL is a web
    // page that would never decode, so APOD's own thumbnail stands in for it.
    val model = if (kind == MediaKind.VIDEO_EMBED) picture.thumbnailUrl else picture.url
    val isVideo = kind == MediaKind.VIDEO_FILE || kind == MediaKind.VIDEO_EMBED

    Box(
        // A video frame has no intrinsic size until it is extracted, and none at all if extraction
        // fails, so the tile reserves its own space instead of collapsing to the badge.
        modifier = if (isVideo) {
            Modifier
                .fillMaxWidth()
                .aspectRatio(VIDEO_TILE_ASPECT_RATIO)
                .background(ApodTheme.colors.surfaceVariant)
        } else {
            Modifier
        },
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = model,
            contentDescription = picture.title,
            modifier = Modifier.fillMaxWidth()
        )
        if (isVideo) {
            Icon(
                painter = painterResource(DesignSystemRes.drawable.ic_play),
                contentDescription = null,
                tint = ApodTheme.colors.onSurfaceVariant,
                modifier = Modifier.size(PlayBadgeSize)
            )
        }
    }
}

private val previewImage = FavouritePictureModel(
    id = 1L,
    copyright = null,
    date = "2026-07-10",
    explanation = "e",
    hdUrl = "",
    mediaType = "image",
    serviceVersion = "v1",
    title = "Preview Favourite",
    url = ""
)

// A direct video file: the tile reserves 16:9 for a frame that has to be extracted first.
private val previewVideoFile = previewImage.copy(
    id = 2L,
    date = "2026-08-21",
    mediaType = "video",
    title = "Preview Clip",
    url = "https://apod.nasa.gov/apod/image/2608/eso2612b.mp4"
)

// An embed: the frame cannot be extracted from a web page, so APOD's own thumbnail stands in.
private val previewVideoEmbed = previewImage.copy(
    id = 3L,
    date = "2026-08-23",
    mediaType = "video",
    title = "Preview Embed",
    url = "https://www.youtube.com/embed/UgxWkOXcdZU",
    thumbnailUrl = "https://img.youtube.com/vi/UgxWkOXcdZU/0.jpg"
)

@Preview
@Composable
private fun FavouritesSuccessPreview() {
    ApodTheme {
        FavouritesScreen(
            uiState = FavouritesUiState(screenState = Success(listOf(previewImage))),
            uiEvent = {}
        )
    }
}

// The mixed grid is the interesting one: only here do the play badge and the reserved video tile show
// up next to a picture tile that takes its height from the image.
@Preview
@Composable
private fun FavouritesMixedMediaPreview() {
    ApodTheme {
        FavouritesScreen(
            uiState = FavouritesUiState(
                screenState = Success(listOf(previewImage, previewVideoFile, previewVideoEmbed))
            ),
            uiEvent = {}
        )
    }
}

@Preview
@Composable
private fun FavouriteVideoTilePreview() {
    ApodTheme {
        FavouriteCard(picture = previewVideoFile, onClick = {})
    }
}

@Preview
@Composable
private fun FavouritePictureTilePreview() {
    ApodTheme {
        FavouriteCard(picture = previewImage, onClick = {})
    }
}

@Preview
@Composable
private fun FavouritesEmptyPreview() {
    ApodTheme {
        FavouritesScreen(uiState = FavouritesUiState(screenState = Empty), uiEvent = {})
    }
}

@Preview
@Composable
private fun FavouritesLoadingPreview() {
    ApodTheme {
        FavouritesScreen(uiState = FavouritesUiState(screenState = Loading), uiEvent = {})
    }
}
