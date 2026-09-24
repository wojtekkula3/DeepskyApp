package com.wojciechkula.deepskyapp.core.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme

// android.content.res.Configuration values, which commonMain cannot reference.
private const val UI_MODE_NIGHT_NO = 0x10
private const val UI_MODE_NIGHT_YES = 0x20

/** Previews [content] in a [Box] on the theme background, following the preview's light/dark `uiMode`. */
@Composable
fun ApodBoxPreview(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) = ApodTheme {
    Box(Modifier.background(ApodTheme.colors.background).then(modifier)) { content() }
}

/** Previews [content] in a [Column] on the theme background, following the preview's light/dark `uiMode`. */
@Composable
fun ApodColumnPreview(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) = ApodTheme {
    Column(Modifier.background(ApodTheme.colors.background).then(modifier), verticalArrangement, horizontalAlignment) {
        content()
    }
}

/** Previews [content] in a [Row] on the theme background, following the preview's light/dark `uiMode`. */
@Composable
fun ApodRowPreview(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.Start,
    verticalAlignment: Alignment.Vertical = Alignment.Top,
    content: @Composable RowScope.() -> Unit
) = ApodTheme {
    Row(Modifier.background(ApodTheme.colors.background).then(modifier), horizontalArrangement, verticalAlignment) {
        content()
    }
}

/** Previews a whole screen, which draws its own background (usually through `Scaffold`). */
@Composable
fun ApodScreenPreview(content: @Composable () -> Unit) = ApodTheme {
    content()
}

/** Renders a component preview twice, in light and in dark mode. */
@Preview(
    name = "1. Light mode",
    uiMode = UI_MODE_NIGHT_NO,
    showBackground = true
)
@Preview(
    name = "2. Dark mode",
    uiMode = UI_MODE_NIGHT_YES,
    showBackground = true
)
annotation class LightDarkPreview

/** Renders a screen preview in light and dark mode on a phone-sized canvas. */
@Preview(
    name = "1. Light mode",
    uiMode = UI_MODE_NIGHT_NO,
    widthDp = 400,
    heightDp = 720,
    showBackground = true
)
@Preview(
    name = "2. Dark mode",
    uiMode = UI_MODE_NIGHT_YES,
    widthDp = 400,
    heightDp = 720,
    showBackground = true
)
annotation class ScreenLightDarkPreview
