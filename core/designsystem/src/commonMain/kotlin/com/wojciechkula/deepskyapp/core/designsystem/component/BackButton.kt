package com.wojciechkula.deepskyapp.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.content_description_back
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_back
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun BackButton(onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        modifier = Modifier.padding(start = ApodTheme.dimensions.marginSmallMedium),
        shape = ApodTheme.shapes.iconButton,
        color = ApodTheme.colors.surface,
        border = BorderStroke(ApodTheme.dimensions.borderSmall, ApodTheme.colors.outline),
        shadowElevation = ApodTheme.elevation.iconButton
    ) {
        Icon(
            painter = painterResource(DesignSystemRes.drawable.ic_back),
            contentDescription = stringResource(DesignSystemRes.string.content_description_back),
            modifier = Modifier
                .padding(ApodTheme.dimensions.marginSmall)
                .size(ApodTheme.dimensions.iconMedium),
            tint = ApodTheme.colors.onSurface
        )
    }
}

@LightDarkPreview
@Composable
private fun BackButtonPreview() = ApodColumnPreview {
    ScreenTitleBar(title = "Title", navigationIcon = { BackButton(onClick = {}) })
}
