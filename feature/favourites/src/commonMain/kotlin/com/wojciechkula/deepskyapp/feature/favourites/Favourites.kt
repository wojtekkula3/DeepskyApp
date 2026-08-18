package com.wojciechkula.deepskyapp.feature.favourites

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.core.mvvm.ActionsEffect
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
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

@Composable
fun Favourites(
    onOpenDetails: (date: String) -> Unit,
    onOpenAbout: () -> Unit,
    viewModel: FavouritesViewModel = koinViewModel()
) {
    val uiState by viewModel.states.collectAsStateWithLifecycle()

    ActionsEffect(viewModel.actions) { action ->
        when (action) {
            is OpenDetails -> onOpenDetails(action.date)
            OpenAbout -> onOpenAbout()
        }
    }

    FavouritesScreen(
        uiState = uiState,
        uiEvent = viewModel::handleUiEvent
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FavouritesScreen(
    uiState: FavouritesUiState,
    uiEvent: (FavouritesUiEvent) -> Unit
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
                }
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
            AsyncImage(
                model = picture.url,
                contentDescription = picture.title,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = picture.title,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            )
            Text(
                text = picture.date,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 4.dp)
            )
        }
    }
}

@Preview
@Composable
private fun FavouritesSuccessPreview() {
    DeepskyTheme {
        FavouritesScreen(
            uiState = FavouritesUiState(
                screenState = Success(
                    listOf(
                        FavouritePictureModel(
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
                    )
                )
            ),
            uiEvent = {}
        )
    }
}

@Preview
@Composable
private fun FavouritesEmptyPreview() {
    DeepskyTheme {
        FavouritesScreen(uiState = FavouritesUiState(screenState = Empty), uiEvent = {})
    }
}

@Preview
@Composable
private fun FavouritesLoadingPreview() {
    DeepskyTheme {
        FavouritesScreen(uiState = FavouritesUiState(screenState = Loading), uiEvent = {})
    }
}
