package com.wojciechkula.deepskyapp.feature.favourites.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel

private const val GRID_COLUMNS = 2

@Composable
internal fun SuccessScreen(
    pictures: List<FavouritePictureModel>,
    onAboutClick: () -> Unit,
    onPictureClick: (date: String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues()
) {
    val layoutDirection = LocalLayoutDirection.current
    // Horizontal and top margins go into the grid's content padding, not a modifier, so the lazy layout's
    // clip does not cut off the cards' shadows.
    val gridPadding = PaddingValues(
        start = contentPadding.calculateStartPadding(layoutDirection) + ApodTheme.dimensions.marginMedium,
        top = contentPadding.calculateTopPadding() + ApodTheme.dimensions.marginSmall,
        end = contentPadding.calculateEndPadding(layoutDirection) + ApodTheme.dimensions.marginMedium,
        bottom = contentPadding.calculateBottomPadding() + ApodTheme.dimensions.marginMedium
    )

    Column(modifier = modifier) {
        ScreenTitle(onAboutClick = onAboutClick)
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(GRID_COLUMNS),
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = gridPadding,
            verticalItemSpacing = ApodTheme.dimensions.marginMedium,
            horizontalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmallMedium)
        ) {
            items(pictures, key = { it.date }) { picture ->
                FavouriteCard(
                    picture = picture,
                    onClick = { onPictureClick(picture.date) }
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun SuccessScreenPreview() = ApodColumnPreview {
    SuccessScreen(
        pictures = previewPictures,
        onAboutClick = {},
        onPictureClick = {},
        modifier = Modifier.fillMaxSize()
    )
}
