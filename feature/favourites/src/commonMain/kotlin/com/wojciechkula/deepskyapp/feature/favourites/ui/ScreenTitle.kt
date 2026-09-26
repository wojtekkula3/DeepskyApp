package com.wojciechkula.deepskyapp.feature.favourites.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_info
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.feature.favourites.resources.Res
import com.wojciechkula.deepskyapp.feature.favourites.resources.favourites_content_description_about
import com.wojciechkula.deepskyapp.feature.favourites.resources.favourites_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ScreenTitle(onAboutClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = ApodTheme.dimensions.marginLarge,
                end = ApodTheme.dimensions.marginSmall,
                top = ApodTheme.dimensions.marginSmall,
                bottom = ApodTheme.dimensions.marginSmall
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.favourites_title),
            modifier = Modifier.weight(1f),
            style = ApodTheme.typography.headlineMedium,
            color = ApodTheme.colors.onSurface
        )
        IconButton(onClick = onAboutClick) {
            Icon(
                painter = painterResource(DesignSystemRes.drawable.ic_info),
                contentDescription = stringResource(Res.string.favourites_content_description_about),
                tint = ApodTheme.colors.onSurface
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun ScreenTitlePreview() = ApodColumnPreview {
    ScreenTitle(onAboutClick = {})
}
