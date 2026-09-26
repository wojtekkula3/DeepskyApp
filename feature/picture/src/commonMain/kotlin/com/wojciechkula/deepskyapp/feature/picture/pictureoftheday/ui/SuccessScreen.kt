package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.ContentCard
import com.wojciechkula.deepskyapp.core.designsystem.component.FavouriteButton
import com.wojciechkula.deepskyapp.core.designsystem.component.InfoRow
import com.wojciechkula.deepskyapp.core.designsystem.component.ScreenTitleBar
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_calendar
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_copyright
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.domain.model.MediaKind
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.model.mediaKind
import com.wojciechkula.deepskyapp.feature.picture.PictureMedia
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiState
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_add_to_favourites
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_copyright
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_date
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_new_picture_in
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_title
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_remove_from_favourites
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SuccessScreen(
    picture: PictureOfTheDayModel,
    uiState: PictureOfTheDayUiState,
    onFavouriteClick: () -> Unit,
    onMediaFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ScreenTitleBar(title = stringResource(Res.string.picture_of_the_day_title))
        MediaCard(picture, uiState, onMediaFailed)
        DetailsCard(picture, uiState, onFavouriteClick)
        if (uiState.timeToNewPicture.isNotEmpty()) {
            Timer(uiState)
        }
    }
}

@Composable
private fun DetailsCard(
    picture: PictureOfTheDayModel,
    uiState: PictureOfTheDayUiState,
    onFavouriteClick: () -> Unit
) {
    ContentCard(modifier = Modifier.padding(top = ApodTheme.dimensions.marginMedium)) {
        Column(modifier = Modifier.padding(ApodTheme.dimensions.marginMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = picture.title,
                    modifier = Modifier.weight(1f),
                    style = ApodTheme.typography.titleLarge,
                    color = ApodTheme.colors.onSurface
                )
                if (mediaKind(picture.mediaType, picture.url) != MediaKind.UNSUPPORTED) {
                    FavouriteButton(
                        isFavourite = uiState.isFavourite,
                        contentDescription = if (uiState.isFavourite) {
                            stringResource(Res.string.picture_remove_from_favourites)
                        } else {
                            stringResource(Res.string.picture_add_to_favourites)
                        },
                        onClick = onFavouriteClick
                    )
                }
            }
            picture.copyright?.let {
                InfoRow(
                    icon = painterResource(DesignSystemRes.drawable.ic_copyright),
                    label = stringResource(Res.string.picture_label_copyright),
                    value = it.trim().replace("\n", " ")
                )
            }
            InfoRow(
                icon = painterResource(DesignSystemRes.drawable.ic_calendar),
                label = stringResource(Res.string.picture_label_date),
                value = picture.date
            )
            Text(
                text = picture.explanation,
                modifier = Modifier.padding(top = ApodTheme.dimensions.marginMedium),
                style = ApodTheme.typography.bodyMedium,
                color = ApodTheme.colors.onSurface,
                textAlign = TextAlign.Justify
            )
        }
    }
}

@Composable
private fun MediaCard(
    picture: PictureOfTheDayModel,
    uiState: PictureOfTheDayUiState,
    onMediaFailed: () -> Unit
) {
    ContentCard {
        PictureMedia(
            url = picture.url,
            mediaType = picture.mediaType,
            title = picture.title,
            thumbnailUrl = picture.thumbnailUrl,
            isOffline = uiState.isOffline,
            onMediaFailed = onMediaFailed
        )
    }
}

@Composable
private fun Timer(uiState: PictureOfTheDayUiState) {
    Text(
        text = stringResource(Res.string.picture_of_the_day_new_picture_in, uiState.timeToNewPicture),
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                top = ApodTheme.dimensions.marginSmallMedium,
                start = ApodTheme.dimensions.marginLarge,
                end = ApodTheme.dimensions.marginLarge
            ),
        style = ApodTheme.typography.bodyMedium,
        textAlign = TextAlign.Center,
        color = ApodTheme.colors.onSurfaceVariant
    )
}

@Composable
private fun SuccessScreenPreview(isFavourite: Boolean) = ApodColumnPreview(
    modifier = Modifier.padding(vertical = ApodTheme.dimensions.marginMedium)
) {
    SuccessScreen(
        picture = previewPicture,
        uiState = PictureOfTheDayUiState(
            screenState = PictureOfTheDayScreenState.Success(previewPicture),
            isFavourite = isFavourite,
            timeToNewPicture = "8h 0min 0s"
        ),
        onFavouriteClick = {},
        onMediaFailed = {}
    )
}

@LightDarkPreview
@Composable
private fun SuccessScreenFavouritePreview() {
    SuccessScreenPreview(isFavourite = true)
}

@LightDarkPreview
@Composable
private fun SuccessScreenNotFavouritePreview() {
    SuccessScreenPreview(isFavourite = false)
}
