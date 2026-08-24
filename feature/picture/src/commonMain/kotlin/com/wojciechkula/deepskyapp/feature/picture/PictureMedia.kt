package com.wojciechkula.deepskyapp.feature.picture

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_play
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.core.media.rememberUrlOpener
import com.wojciechkula.deepskyapp.domain.model.MediaKind
import com.wojciechkula.deepskyapp.domain.model.mediaKind
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_media_not_available
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_no_internet
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_open_video
import net.engawapg.lib.zoomable.rememberZoomState
import net.engawapg.lib.zoomable.zoomable
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val PlayBadgeSize = 64.dp

/**
 * Renders an APOD entry's media.
 *
 * [thumbnailUrl] is APOD's own `thumbnail_url`, the only thumbnail source that covers every embed
 * provider; it is null for a direct video file, whose poster comes from an extracted frame instead.
 *
 * [isOffline] decides what a failed load says, because neither player nor image loader reports a cause
 * we could trust: connectivity is asked at the moment of failure instead. [onMediaFailed] lets a caller
 * escalate — the day screen turns it into a full-screen connectivity error, while the details screen
 * ignores it and keeps the text it already has from the database.
 */
@Composable
internal fun PictureMedia(
    url: String,
    mediaType: String,
    title: String,
    thumbnailUrl: String?,
    isOffline: Boolean,
    modifier: Modifier = Modifier,
    onMediaFailed: () -> Unit = {}
) {
    when (mediaKind(mediaType, url)) {
        MediaKind.IMAGE -> ZoomablePicture(
            url = url,
            title = title,
            isOffline = isOffline,
            onMediaFailed = onMediaFailed,
            modifier = modifier.fillMaxWidth()
        )

        // No zoom on video: pinching a looping clip has no use here.
        MediaKind.VIDEO_FILE -> PlayableVideo(
            url = url,
            isOffline = isOffline,
            onMediaFailed = onMediaFailed,
            modifier = modifier.fillMaxWidth()
        )

        MediaKind.VIDEO_EMBED -> EmbeddedVideoPoster(
            url = url,
            title = title,
            thumbnailUrl = thumbnailUrl,
            modifier = modifier.fillMaxWidth()
        )

        // Not a network problem, so this one never reads as a connectivity failure.
        MediaKind.UNSUPPORTED -> MediaMessage(
            text = stringResource(Res.string.picture_media_not_available),
            modifier = modifier
        )
    }
}

@Composable
private fun ZoomablePicture(
    url: String,
    title: String,
    isOffline: Boolean,
    onMediaFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Keyed on url so a new entry gets a fresh attempt instead of inheriting the previous failure.
    var failed by remember(url) { mutableStateOf(false) }

    if (failed) {
        MediaFailure(isOffline = isOffline, modifier = modifier)
    } else {
        AsyncImage(
            model = url,
            contentDescription = title,
            onError = {
                failed = true
                onMediaFailed()
            },
            modifier = modifier.zoomable(rememberZoomState())
        )
    }
}

// An embed URL is a web page, not a stream, so the only thing to offer is a hand-off to the platform.
// A thumbnail that fails to load is cosmetic here: the badge and the tap target survive it, so it does
// not escalate as a media failure.
@Composable
private fun EmbeddedVideoPoster(
    url: String,
    title: String,
    thumbnailUrl: String?,
    modifier: Modifier = Modifier
) {
    val openUrl = rememberUrlOpener()

    Box(
        modifier = modifier.clickable(onClickLabel = stringResource(Res.string.picture_open_video)) {
            openUrl(url)
        },
        contentAlignment = Alignment.Center
    ) {
        if (thumbnailUrl != null) {
            AsyncImage(
                model = thumbnailUrl,
                contentDescription = title,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Icon(
            painter = painterResource(DesignSystemRes.drawable.ic_play),
            modifier = Modifier
                .padding(24.dp)
                .size(PlayBadgeSize),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
internal fun MediaFailure(
    isOffline: Boolean,
    modifier: Modifier = Modifier
) {
    MediaMessage(
        text = if (isOffline) {
            stringResource(Res.string.picture_no_internet)
        } else {
            stringResource(Res.string.picture_media_not_available)
        },
        modifier = modifier
    )
}

@Composable
private fun MediaMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text,
        modifier = modifier.padding(24.dp),
        textAlign = TextAlign.Center
    )
}

// The previews go through PictureMedia rather than calling a branch directly, so the classification is
// exercised too: a wrong `mediaKind` shows up here as the wrong preview.

@Preview
@Composable
private fun PictureMediaVideoFilePreview() {
    DeepskyTheme {
        PictureMedia(
            url = "https://apod.nasa.gov/apod/image/2608/eso2612b.mp4",
            mediaType = "video",
            title = "Preview clip",
            thumbnailUrl = null,
            isOffline = false
        )
    }
}

@Preview
@Composable
private fun PictureMediaVideoEmbedPreview() {
    DeepskyTheme {
        PictureMedia(
            url = "https://www.youtube.com/embed/UgxWkOXcdZU",
            mediaType = "video",
            title = "Preview embed",
            thumbnailUrl = null,
            isOffline = false
        )
    }
}

// APOD really does publish `media_type: other`, so this branch is reachable in principle — though such
// an entry has so far always arrived with no `url` at all, which fails to parse long before this.
@Preview
@Composable
private fun PictureMediaUnsupportedPreview() {
    DeepskyTheme {
        PictureMedia(
            url = "https://apod.nasa.gov/apod/ap241023.html",
            mediaType = "other",
            title = "Preview unsupported",
            thumbnailUrl = null,
            isOffline = false
        )
    }
}

@Preview
@Composable
private fun MediaFailureOfflinePreview() {
    DeepskyTheme {
        MediaFailure(isOffline = true)
    }
}

@Preview
@Composable
private fun MediaFailureUnavailablePreview() {
    DeepskyTheme {
        MediaFailure(isOffline = false)
    }
}
