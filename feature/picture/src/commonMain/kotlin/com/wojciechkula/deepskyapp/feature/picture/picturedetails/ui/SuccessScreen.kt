package com.wojciechkula.deepskyapp.feature.picture.picturedetails.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
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
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.BackButton
import com.wojciechkula.deepskyapp.core.designsystem.component.ContentCard
import com.wojciechkula.deepskyapp.core.designsystem.component.FavouriteButton
import com.wojciechkula.deepskyapp.core.designsystem.component.InfoRow
import com.wojciechkula.deepskyapp.core.designsystem.component.ScreenTitleBar
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_calendar
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_copyright
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.feature.picture.PictureMedia
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiState
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_dialog_cancel
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_dialog_confirm
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_dialog_message
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_dialog_title
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_title
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_copyright
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_label_date
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_remove_from_favourites
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun SuccessScreen(
    picture: FavouritePictureModel,
    uiState: PictureDetailsUiState,
    onBackClick: () -> Unit,
    onDeleteConfirmed: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        ScreenTitleBar(
            title = stringResource(Res.string.picture_details_title),
            navigationIcon = { BackButton(onClick = onBackClick) }
        )
        Spacer(modifier = Modifier.height(ApodTheme.dimensions.marginSmall))
        MediaCard(picture, uiState)
        DetailsCard(picture, onFavouriteClick = { showDeleteDialog = true })
    }

    if (showDeleteDialog) {
        DeleteDialog(
            onConfirm = {
                showDeleteDialog = false
                onDeleteConfirmed()
            },
            onDismiss = { showDeleteDialog = false }
        )
    }
}

@Composable
private fun MediaCard(
    picture: FavouritePictureModel,
    uiState: PictureDetailsUiState
) {
    ContentCard {
        // No onMediaFailed here on purpose: a favourite's text comes from the database and stays
        // readable offline, so only the media slot reports the failure.
        PictureMedia(
            url = picture.url,
            hdUrl = picture.hdUrl,
            mediaType = picture.mediaType,
            title = picture.title,
            thumbnailUrl = picture.thumbnailUrl,
            isOffline = uiState.isOffline
        )
    }
}

@Composable
private fun DetailsCard(
    picture: FavouritePictureModel,
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
                FavouriteButton(
                    isFavourite = true,
                    contentDescription = stringResource(Res.string.picture_remove_from_favourites),
                    onClick = onFavouriteClick
                )
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
private fun DeleteDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(Res.string.picture_details_dialog_title)) },
        text = { Text(stringResource(Res.string.picture_details_dialog_message)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(Res.string.picture_details_dialog_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(Res.string.picture_details_dialog_cancel))
            }
        },
        shape = ApodTheme.shapes.large,
        containerColor = ApodTheme.colors.surface,
        titleContentColor = ApodTheme.colors.onSurface,
        textContentColor = ApodTheme.colors.onSurfaceVariant
    )
}

@Composable
private fun SuccessScreenPreview(picture: FavouritePictureModel) = ApodColumnPreview(
    modifier = Modifier.padding(vertical = ApodTheme.dimensions.marginMedium)
) {
    SuccessScreen(
        picture = picture,
        uiState = PictureDetailsUiState(screenState = PictureDetailsScreenState.Success(picture)),
        onBackClick = {},
        onDeleteConfirmed = {}
    )
}

@LightDarkPreview
@Composable
private fun SuccessScreenImagePreview() {
    SuccessScreenPreview(picture = previewPicture)
}

@LightDarkPreview
@Composable
private fun SuccessScreenNoCopyrightPreview() {
    SuccessScreenPreview(picture = previewPicture.copy(copyright = null))
}
