package com.wojciechkula.deepskyapp.feature.picture

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil3.compose.AsyncImage
import coil3.compose.AsyncImagePainter
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.size.Size
import com.wojciechkula.deepskyapp.core.designsystem.ApodBoxPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.MediaLoadingBox
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_close
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_open_in_full
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_image_enter_fullscreen
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_image_exit_fullscreen
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val BUTTON_SCRIM_ALPHA = 0.5f
private const val ICON_ALPHA = 0.8f
private const val PREVIEW_ASPECT_RATIO = 1.5f
private val ScrimButtonSize = 42.dp

/**
 * An image in its card, with a button that opens it full screen — the only place it can be zoomed.
 * The card shows [url]; full screen adds [hdUrl] on top of it, since that is the copy worth zooming into.
 */
@Composable
internal fun ExpandablePicture(
    url: String,
    hdUrl: String,
    title: String,
    isOffline: Boolean,
    onMediaFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Keyed on url so a new entry gets a fresh attempt instead of inheriting the previous failure.
    var failed by remember(url) { mutableStateOf(false) }
    var isLoading by remember(url) { mutableStateOf(true) }
    var fullscreen by rememberSaveable(url) { mutableStateOf(false) }

    if (failed) {
        MediaFailure(
            isOffline = isOffline,
            onRetry = {
                failed = false
                isLoading = true
            },
            modifier = modifier
        )
        return
    }

    Box(modifier = modifier) {
        MediaLoadingBox(isLoading = isLoading) {
            AsyncImage(
                model = url,
                contentDescription = title,
                // Fit would take the height from the bitmap's own size, so an image narrower than the card
                // gets side bars; FillWidth scales it to the card's width instead.
                contentScale = ContentScale.FillWidth,
                onSuccess = { isLoading = false },
                onError = {
                    isLoading = false
                    failed = true
                    onMediaFailed()
                },
                modifier = Modifier.fillMaxWidth()
            )
        }
        if (!isLoading) {
            EnterFullscreenButton(
                onClick = { fullscreen = true },
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }

    if (fullscreen) {
        FullscreenPicture(url = url, hdUrl = hdUrl, title = title, onDismiss = { fullscreen = false })
    }
}

@Composable
private fun EnterFullscreenButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ScrimIconButton(
        painter = painterResource(DesignSystemRes.drawable.ic_open_in_full),
        contentDescription = stringResource(Res.string.picture_image_enter_fullscreen),
        onClick = onClick,
        modifier = modifier.padding(ApodTheme.dimensions.marginSmall)
    )
}

@Composable
private fun FullscreenPicture(
    url: String,
    hdUrl: String,
    title: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = fullscreenDialogProperties()
    ) {
        FullscreenPictureContent(url = url, hdUrl = hdUrl, title = title, onDismiss = onDismiss)
    }
}

@Composable
private fun FullscreenPictureContent(
    url: String,
    hdUrl: String,
    title: String,
    onDismiss: () -> Unit
) {
    val zoomState = rememberZoomState()
    // APOD leaves hdurl out for some entries, and the DTO turns that into a blank string.
    val sharperUrl = hdUrl.takeIf { it.isNotBlank() && it != url }
    var isLoading by remember(url, sharperUrl) { mutableStateOf(true) }
    // Without the bitmap's size the zoom bounds are the whole screen, so a panned image could slide off
    // into the letterbox. Both copies share an aspect ratio, so either one's size will do.
    val onPictureLoaded: (AsyncImagePainter.State.Success) -> Unit = { state ->
        zoomState.setContentSize(state.painter.intrinsicSize)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ApodTheme.colors.scrim)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zoomable(zoomState)
        ) {
            // The card's copy is already cached, so it shows at once and stays behind a failed HD load.
            AsyncImage(
                model = url,
                contentDescription = title,
                contentScale = ContentScale.Fit,
                onSuccess = { state ->
                    onPictureLoaded(state)
                    if (sharperUrl == null) isLoading = false
                },
                onError = { if (sharperUrl == null) isLoading = false },
                modifier = Modifier.fillMaxSize()
            )
            sharperUrl?.let { HdPicture(url = it, onLoaded = onPictureLoaded, onSettled = { isLoading = false }) }
        }
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = ApodTheme.colors.onScrim
            )
        }
        ScrimIconButton(
            painter = painterResource(DesignSystemRes.drawable.ic_close),
            contentDescription = stringResource(Res.string.picture_image_exit_fullscreen),
            onClick = onDismiss,
            modifier = Modifier
                .align(Alignment.TopStart)
                .safeDrawingPadding()
                .padding(ApodTheme.dimensions.marginMedium)
        )
    }
}

@Composable
private fun HdPicture(
    url: String,
    onLoaded: (AsyncImagePainter.State.Success) -> Unit,
    onSettled: () -> Unit
) {
    val context = LocalPlatformContext.current
    // Coil decodes to the view's size by default, i.e. the screen's, which would throw away exactly the
    // detail a zoom is for. Coil still caps the bitmap at its default 4096 px maxBitmapSize.
    val request = remember(url, context) {
        ImageRequest.Builder(context)
            .data(url)
            .size(Size.ORIGINAL)
            .build()
    }
    AsyncImage(
        model = request,
        // The copy underneath already describes the picture.
        contentDescription = null,
        contentScale = ContentScale.Fit,
        onSuccess = { state ->
            onLoaded(state)
            onSettled()
        },
        onError = { onSettled() },
        modifier = Modifier.fillMaxSize()
    )
}

@Composable
private fun ScrimIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .minimumInteractiveComponentSize()
            .size(ScrimButtonSize)
            .clip(ApodTheme.shapes.iconButton)
            .background(ApodTheme.colors.scrim.copy(alpha = BUTTON_SCRIM_ALPHA))
            .clickable(role = Role.Button, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.size(ApodTheme.dimensions.iconMedium),
            tint = ApodTheme.colors.onScrim.copy(alpha = ICON_ALPHA)
        )
    }
}

@LightDarkPreview
@Composable
private fun EnterFullscreenButtonPreview() = ApodBoxPreview {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(PREVIEW_ASPECT_RATIO)
            .background(ApodTheme.colors.surfaceVariant)
    ) {
        EnterFullscreenButton(onClick = {}, modifier = Modifier.align(Alignment.BottomEnd))
    }
}

@LightDarkPreview
@Composable
private fun FullscreenPictureContentPreview() = ApodBoxPreview {
    FullscreenPictureContent(
        url = "https://apod.nasa.gov/apod/image/preview.jpg",
        hdUrl = "https://apod.nasa.gov/apod/image/preview_hd.jpg",
        title = "Preview",
        onDismiss = {}
    )
}
