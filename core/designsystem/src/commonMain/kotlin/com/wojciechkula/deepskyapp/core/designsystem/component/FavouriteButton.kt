package com.wojciechkula.deepskyapp.core.designsystem.component

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import com.wojciechkula.deepskyapp.core.designsystem.ApodRowPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite_border
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import org.jetbrains.compose.resources.painterResource

/**
 * A heart icon button: filled in the favourite colour when [isFavourite], outlined otherwise.
 * [contentDescription] should name what a tap does, since that depends on the caller.
 */
@Composable
fun FavouriteButton(
    isFavourite: Boolean,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(onClick = onClick) {
        Icon(
            painter = painterResource(
                if (isFavourite) DesignSystemRes.drawable.ic_favourite else DesignSystemRes.drawable.ic_favourite_border
            ),
            contentDescription = contentDescription,
            tint = if (isFavourite) ApodTheme.colors.favorite else ApodTheme.colors.onSurface
        )
    }
}

@LightDarkPreview
@Composable
private fun FavouriteButtonPreview() = ApodRowPreview {
    FavouriteButton(isFavourite = true, contentDescription = "Remove from favourites", onClick = {})
    FavouriteButton(isFavourite = false, contentDescription = "Add to favourites", onClick = {})
}
