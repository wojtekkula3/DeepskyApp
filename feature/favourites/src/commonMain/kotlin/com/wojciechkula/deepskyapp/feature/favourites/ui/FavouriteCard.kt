package com.wojciechkula.deepskyapp.feature.favourites.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wojciechkula.deepskyapp.core.designsystem.ApodRowPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_play
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.MediaKind
import com.wojciechkula.deepskyapp.domain.model.mediaKind
import org.jetbrains.compose.resources.painterResource

private const val VIDEO_TILE_ASPECT_RATIO = 16f / 9f
private const val PLAY_BADGE_SCRIM_ALPHA = 0.5f
private const val TITLE_MAX_LINES = 2
private val PreviewCardWidth = 180.dp

@Composable
internal fun FavouriteCard(
    picture: FavouritePictureModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = ApodTheme.shapes.medium,
        color = ApodTheme.colors.surface,
        border = BorderStroke(ApodTheme.dimensions.borderSmall, ApodTheme.colors.outline),
        shadowElevation = ApodTheme.elevation.galleryCard
    ) {
        Column {
            Box {
                FavouriteThumbnail(picture = picture)
                FavouriteBadge(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(ApodTheme.dimensions.marginSmall)
                )
            }
            Column(modifier = Modifier.padding(ApodTheme.dimensions.marginSmallMedium)) {
                Text(
                    text = picture.title,
                    style = ApodTheme.typography.bodyLargeBold,
                    color = ApodTheme.colors.onSurface,
                    maxLines = TITLE_MAX_LINES,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = picture.date,
                    modifier = Modifier.padding(top = ApodTheme.dimensions.marginSmall),
                    style = ApodTheme.typography.bodyMedium,
                    color = ApodTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun FavouriteThumbnail(picture: FavouritePictureModel) {
    val kind = mediaKind(picture.mediaType, picture.url)
    // An mp4 resolves through the video-frame fetcher on the singleton loader; an embed URL is a web
    // page that would never decode, so APOD's own thumbnail stands in for it.
    val model = if (kind == MediaKind.VIDEO_EMBED) picture.thumbnailUrl else picture.url

    if (kind == MediaKind.VIDEO_FILE || kind == MediaKind.VIDEO_EMBED) {
        // A video frame has no intrinsic size until it is extracted, and none at all if extraction
        // fails, so the tile reserves its own space instead of collapsing to the badge.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(VIDEO_TILE_ASPECT_RATIO)
                .clip(ApodTheme.shapes.medium)
                .background(ApodTheme.colors.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = model,
                contentDescription = picture.title,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            PlayBadge()
        }
    } else {
        AsyncImage(
            model = model,
            contentDescription = picture.title,
            modifier = Modifier
                .fillMaxWidth()
                .clip(ApodTheme.shapes.medium)
        )
    }
}

@Composable
private fun FavouriteBadge(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = ApodTheme.shapes.iconButton,
        color = ApodTheme.colors.surface,
        shadowElevation = ApodTheme.elevation.iconButton
    ) {
        Icon(
            painter = painterResource(DesignSystemRes.drawable.ic_favourite),
            contentDescription = null,
            modifier = Modifier
                .padding(ApodTheme.dimensions.marginSmall)
                .size(ApodTheme.dimensions.iconSmall),
            tint = ApodTheme.colors.favorite
        )
    }
}

@Composable
private fun PlayBadge() {
    Box(
        modifier = Modifier
            .size(ApodTheme.dimensions.iconExtraLarge)
            .background(
                color = ApodTheme.colors.scrim.copy(alpha = PLAY_BADGE_SCRIM_ALPHA),
                shape = ApodTheme.shapes.iconButton
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(DesignSystemRes.drawable.ic_play),
            contentDescription = null,
            modifier = Modifier.size(ApodTheme.dimensions.iconMedium),
            tint = ApodTheme.colors.onScrim
        )
    }
}

@Composable
private fun FavouriteCardPreview(picture: FavouritePictureModel) = ApodRowPreview(
    modifier = Modifier.padding(ApodTheme.dimensions.marginMedium)
) {
    FavouriteCard(
        picture = picture,
        onClick = {},
        modifier = Modifier.width(PreviewCardWidth)
    )
}

@LightDarkPreview
@Composable
private fun FavouriteVideoFileCardPreview() {
    FavouriteCardPreview(picture = previewVideoFile)
}

@LightDarkPreview
@Composable
private fun FavouriteVideoEmbedCardPreview() {
    FavouriteCardPreview(picture = previewVideoEmbed)
}
