package com.wojciechkula.deepskyapp.core.designsystem.layout

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * How much of a screen's bottom an app-level overlay covers — the floating navigation card — measured
 * from where the screen's viewport ends. The host provides it; [com.wojciechkula.deepskyapp.core.designsystem.component.ApodScaffold]
 * keeps its screen's content clear of it.
 */
val LocalBottomOverlaySpace = compositionLocalOf { 0.dp }

/**
 * The space a screen leaves below its content: [endPadding] above whatever overlay [LocalBottomOverlaySpace]
 * reports, but never less than the system's bottom inset. Pass it inside a `verticalScroll` or as a lazy
 * layout's bottom `contentPadding`, so content scrolls under the gesture bar, iOS's home indicator and the
 * overlay, and its end stops clear of them.
 *
 * The inset used is the one content may scroll under — the whole bottom inset minus Android's
 * three-button bar (`tappableElement`). It assumes the host consumed that inset, so `Scaffold`'s own
 * bottom padding, applied outside the scroll, is exactly the three-button bar that content must stop at.
 */
@Composable
internal fun bottomContentPadding(endPadding: Dp = 0.dp): Dp {
    val underlappedInset = WindowInsets.safeDrawing
        .exclude(WindowInsets.tappableElement)
        .only(WindowInsetsSides.Bottom)
        .asPaddingValues()
        .calculateBottomPadding()
    return maxOf(underlappedInset, LocalBottomOverlaySpace.current + endPadding)
}
