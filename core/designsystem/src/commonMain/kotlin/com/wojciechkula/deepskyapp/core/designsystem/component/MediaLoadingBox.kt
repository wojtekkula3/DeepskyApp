package com.wojciechkula.deepskyapp.core.designsystem.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview

private const val LOADING_ASPECT_RATIO = 1.5f

/**
 * Draws [content] — typically a remote image — with a spinner in its place while [isLoading].
 *
 * An image with no bitmap yet has no height of its own, so the slot is held rectangle until the load
 * settles. The rectangle is a sibling of [content], not its container, so the image is still measured
 * against the caller's constraints and the loader does not shrink the size it decodes at.
 */
@Composable
fun MediaLoadingBox(
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Box(modifier = modifier) {
        content()
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(LOADING_ASPECT_RATIO),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun MediaLoadingBoxPreview() = ApodColumnPreview {
    MediaLoadingBox(isLoading = true) {}
}
