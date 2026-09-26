package com.wojciechkula.deepskyapp.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_back
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_info
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import org.jetbrains.compose.resources.painterResource

/**
 * A screen's headline [title] with an optional [navigationIcon] before it and [action] after it,
 * both expected to be `IconButton`s. The margin on a side with a button shrinks so the button's own
 * touch padding lines its icon up with the content below.
 */
@Composable
fun ScreenTitleBar(
    title: String,
    modifier: Modifier = Modifier,
    navigationIcon: (@Composable () -> Unit)? = null,
    action: (@Composable () -> Unit)? = null
) {
    val hasButton = navigationIcon != null || action != null
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = if (navigationIcon != null) ApodTheme.dimensions.marginSmall else ApodTheme.dimensions.marginLarge,
                end = if (action != null) ApodTheme.dimensions.marginSmall else ApodTheme.dimensions.marginLarge,
                top = if (hasButton) ApodTheme.dimensions.marginSmall else ApodTheme.dimensions.marginMedium,
                bottom = if (hasButton) ApodTheme.dimensions.marginSmall else ApodTheme.dimensions.marginMedium
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall)
    ) {
        navigationIcon?.invoke()
        Text(
            text = title,
            modifier = Modifier.weight(1f),
            style = ApodTheme.typography.headlineMedium,
            color = ApodTheme.colors.onSurface
        )
        action?.invoke()
    }
}

@Composable
private fun PreviewIconButton(icon: Painter) {
    IconButton(onClick = {}) {
        Icon(painter = icon, contentDescription = null, tint = ApodTheme.colors.onSurface)
    }
}

@LightDarkPreview
@Composable
private fun ScreenTitleBarPreview() = ApodColumnPreview {
    ScreenTitleBar(title = "Title only")
    ScreenTitleBar(
        title = "With navigation",
        navigationIcon = { PreviewIconButton(painterResource(DesignSystemRes.drawable.ic_back)) }
    )
    ScreenTitleBar(
        title = "With action",
        action = { PreviewIconButton(painterResource(DesignSystemRes.drawable.ic_info)) }
    )
}
