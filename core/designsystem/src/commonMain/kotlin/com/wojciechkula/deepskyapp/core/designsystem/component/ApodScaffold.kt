package com.wojciechkula.deepskyapp.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.wojciechkula.deepskyapp.core.designsystem.ApodScreenPreview
import com.wojciechkula.deepskyapp.core.designsystem.ScreenLightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.layout.bottomContentPadding

/**
 * A screen's `Scaffold` that owns all of its window-inset handling. [content] picks the ready-made modifier
 * matching each screen state from [ApodScaffoldScope], so no screen computes a padding itself: content
 * scrolls under the gesture bar, iOS's home indicator and the floating navigation, and ends clear of them.
 *
 * @param contentEndPadding the space left below scrollable content once it is scrolled to the end, on top
 * of any overlay; the system's bottom inset may overlap it.
 */
@Composable
fun ApodScaffold(
    modifier: Modifier = Modifier,
    contentEndPadding: Dp = 0.dp,
    content: @Composable ApodScaffoldScope.() -> Unit
) {
    Scaffold(modifier = modifier) { padding ->
        val viewport = Modifier
            .fillMaxSize()
            .padding(padding)
        val contentBottom = bottomContentPadding(contentEndPadding)
        Box(modifier = Modifier.fillMaxSize()) {
            ApodScaffoldScope(
                boxScope = this,
                padding = padding,
                screenModifier = viewport.padding(bottom = bottomContentPadding()),
                scrollableModifier = viewport
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = contentBottom),
                lazyModifier = viewport,
                lazyContentPadding = PaddingValues(bottom = contentBottom)
            ).content()
        }
    }
}

/**
 * The modifiers an [ApodScaffold] hands its content, one per kind of screen state.
 *
 * @property padding the raw `Scaffold` padding, for an overlay aligned to an edge, such as a top snackbar.
 * @property screenModifier fills the area clear of the system bars and any overlay; for a state that
 * does not scroll, such as a centred loader.
 * @property scrollableModifier fills the screen and scrolls vertically; for a state whose content may
 * outgrow it.
 * @property lazyModifier fills the screen for a lazy layout, which takes [lazyContentPadding] as its
 * `contentPadding` so that its items scroll under the bottom bars.
 */
@Stable
class ApodScaffoldScope internal constructor(
    boxScope: BoxScope,
    val padding: PaddingValues,
    val screenModifier: Modifier,
    val scrollableModifier: Modifier,
    val lazyModifier: Modifier,
    val lazyContentPadding: PaddingValues
) : BoxScope by boxScope

@ScreenLightDarkPreview
@Composable
private fun ApodScaffoldPreview() = ApodScreenPreview {
    ApodScaffold {
        Text(text = "Content", modifier = scrollableModifier)
    }
}
