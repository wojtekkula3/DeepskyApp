package com.wojciechkula.deepskyapp.feature.favourites.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.ScreenTitleBar
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
    ScreenTitleBar(
        title = stringResource(Res.string.favourites_title),
        action = { AboutButton(onClick = onAboutClick) }
    )
}

@Composable
private fun AboutButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.padding(end = ApodTheme.dimensions.marginSmall),
        shape = ApodTheme.shapes.iconButton,
        color = ApodTheme.colors.surface,
        border = BorderStroke(ApodTheme.dimensions.borderSmall, ApodTheme.colors.outline)
    ) {
        Icon(
            painter = painterResource(DesignSystemRes.drawable.ic_info),
            contentDescription = stringResource(Res.string.favourites_content_description_about),
            modifier = Modifier
                .padding(ApodTheme.dimensions.marginSmall)
                .size(ApodTheme.dimensions.iconMedium),
            tint = ApodTheme.colors.onSurface
        )
    }
}

@LightDarkPreview
@Composable
private fun ScreenTitlePreview() = ApodColumnPreview {
    ScreenTitle(onAboutClick = {})
}
