package com.wojciechkula.deepskyapp.feature.picture

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_backward_5_sec
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_close
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_forward_5_sec
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_fullscreen
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_pause
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_play
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_volume_off
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_volume_on
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import com.wojciechkula.deepskyapp.core.media.VideoPlayerState
import com.wojciechkula.deepskyapp.core.media.rememberVideoPlayerState
import com.wojciechkula.deepskyapp.core.media.seekBy
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_enter_fullscreen
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_exit_fullscreen
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_forward
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_mute
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_pause
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_play
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_position
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_rewind
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_seek_bar
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_unknown_duration
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_video_unmute
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private const val SEEK_STEP_MILLIS = 5_000L
private const val INACTIVE_TRACK_ALPHA = 0.3f
private val ControlIconSize = 32.dp
private val SeekIconSize = 32.dp

/**
 * A direct video file: autoplay, looping and silent in the card, with everything else behind the
 * fullscreen button.
 *
 * The player comes from [rememberVideoPlayerState] one level above both places it can be drawn, so
 * opening and leaving full screen keeps the same playback rather than starting a second one.
 */
@Composable
internal fun PlayableVideo(
    url: String,
    isOffline: Boolean,
    onMediaFailed: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Keyed on url so a new entry gets a fresh attempt instead of inheriting the previous failure.
    var failed by remember(url) { mutableStateOf(false) }

    if (failed) {
        MediaFailure(isOffline = isOffline, modifier = modifier)
        return
    }

    // The preview renderer has no platform host to build a player on, so it gets the same card drawn
    // around a stand-in. Without this, a screen preview holding a video file renders nothing at all.
    if (LocalInspectionMode.current) {
        VideoCard(state = PreviewVideoPlayerState(), onEnterFullscreen = {}, modifier = modifier)
        return
    }

    val state = rememberVideoPlayerState(url) {
        failed = true
        onMediaFailed()
    }

    var hostResumed by remember { mutableStateOf(false) }
    var wantsPlayback by remember(url) { mutableStateOf(true) }
    var fullscreen by rememberSaveable(url) { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        hostResumed = true
        onPauseOrDispose { hostResumed = false }
    }

    LaunchedEffect(state, hostResumed, wantsPlayback) {
        if (hostResumed && wantsPlayback) state.play() else state.pause()
    }

    if (fullscreen) {
        FullscreenVideo(
            state = state,
            onTogglePlayback = {
                // Driving the player directly as well as the flag: losing audio focus stops playback
                // without the flag changing, and then only a direct call can start it again.
                wantsPlayback = !state.isPlaying
                if (state.isPlaying) state.pause() else state.play()
            },
            onDismiss = {
                fullscreen = false
                // The card has no pause button, so a paused card would be frozen with no way out, and
                // an unmuted one would make noise in a scrolling list.
                wantsPlayback = true
                state.isMuted = true
            }
        )
    } else {
        VideoCard(
            state = state,
            onEnterFullscreen = { fullscreen = true },
            modifier = modifier
        )
    }
}

@Composable
private fun VideoCard(
    state: VideoPlayerState,
    onEnterFullscreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        state.Surface(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(state.aspectRatio)
        )
        OverlayIconButton(
            painter = painterResource(DesignSystemRes.drawable.ic_fullscreen),
            contentDescription = stringResource(Res.string.picture_video_enter_fullscreen),
            onClick = onEnterFullscreen,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

@Composable
private fun FullscreenVideo(
    state: VideoPlayerState,
    onTogglePlayback: () -> Unit,
    onDismiss: () -> Unit
) {
    // A dialog rather than a screen-level overlay: dismissOnBackPress covers Android's system back on
    // its own, so no back handler is needed here. Verified on the iOS simulator that an interop view
    // draws correctly inside it — the dialog's content area there is the safe area, so its own dim
    // shows through over the status bar and home indicator.
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false, dismissOnClickOutside = false)
    ) {
        FullscreenVideoContent(
            state = state,
            onTogglePlayback = onTogglePlayback,
            onDismiss = onDismiss
        )
    }
}

// Split from the dialog so it can be previewed: the preview renderer does not open dialog windows.
@Composable
private fun FullscreenVideoContent(
    state: VideoPlayerState,
    onTogglePlayback: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.scrim)
    ) {
        state.Surface(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth()
                .aspectRatio(state.aspectRatio)
        )
        Box(modifier = Modifier.fillMaxSize().safeDrawingPadding()) {
            OverlayIconButton(
                painter = painterResource(DesignSystemRes.drawable.ic_close),
                contentDescription = stringResource(Res.string.picture_video_exit_fullscreen),
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopStart)
            )
            OverlayIconButton(
                painter = painterResource(
                    if (state.isMuted) DesignSystemRes.drawable.ic_volume_off else DesignSystemRes.drawable.ic_volume_on
                ),
                contentDescription = stringResource(
                    if (state.isMuted) Res.string.picture_video_unmute else Res.string.picture_video_mute
                ),
                onClick = { state.isMuted = !state.isMuted },
                modifier = Modifier.align(Alignment.TopEnd)
            )
            VideoControls(
                state = state,
                onTogglePlayback = onTogglePlayback,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
private fun VideoControls(
    state: VideoPlayerState,
    onTogglePlayback: () -> Unit,
    modifier: Modifier = Modifier
) {
    // The position is only tracked while these controls are on screen: the card shows no time, so
    // polling for it there would cost a platform call four times a second for nothing.
    LaunchedEffect(state) { state.trackTimeline() }

    // While a finger is down the drag owns the bar, otherwise the polled position would fight it.
    var dragProgress by remember { mutableStateOf<Float?>(null) }
    val hasDuration = state.durationMillis > 0L
    val progress = dragProgress ?: playbackProgress(state.positionMillis, state.durationMillis)
    val shownMillis = dragProgress?.let { (it * state.durationMillis).toLong() } ?: state.positionMillis
    val seekBarLabel = stringResource(Res.string.picture_video_seek_bar)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 24.dp)
    ) {
        Slider(
            value = progress,
            onValueChange = { dragProgress = it },
            onValueChangeFinished = {
                dragProgress?.let { state.seekTo((it * state.durationMillis).toLong()) }
                dragProgress = null
            },
            enabled = hasDuration,
            // The remaining track needs setting too: left at its default it is a theme colour, which
            // on a black letterbox reads as an unrelated stripe rather than the rest of the clip.
            colors = SliderDefaults.colors(
                thumbColor = OverlayContentColour,
                activeTrackColor = OverlayContentColour,
                inactiveTrackColor = OverlayContentColour.copy(alpha = INACTIVE_TRACK_ALPHA)
            ),
            modifier = Modifier.semantics { contentDescription = seekBarLabel }
        )
        Text(
            text = stringResource(
                Res.string.picture_video_position,
                formatPlaybackTime(shownMillis),
                if (hasDuration) {
                    formatPlaybackTime(state.durationMillis)
                } else {
                    stringResource(Res.string.picture_video_unknown_duration)
                }
            ),
            color = OverlayContentColour,
            style = MaterialTheme.typography.labelLarge
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            SeekButton(
                painter = painterResource(DesignSystemRes.drawable.ic_backward_5_sec),
                contentDescription = stringResource(Res.string.picture_video_rewind),
                onClick = { state.seekBy(-SEEK_STEP_MILLIS) }
            )
            OverlayIconButton(
                painter = painterResource(
                    if (state.isPlaying) DesignSystemRes.drawable.ic_pause else DesignSystemRes.drawable.ic_play
                ),
                contentDescription = stringResource(
                    if (state.isPlaying) Res.string.picture_video_pause else Res.string.picture_video_play
                ),
                onClick = onTogglePlayback,
                iconSize = ControlIconSize
            )
            SeekButton(
                painter = painterResource(DesignSystemRes.drawable.ic_forward_5_sec),
                contentDescription = stringResource(Res.string.picture_video_forward),
                onClick = { state.seekBy(SEEK_STEP_MILLIS) }
            )
        }
    }
}

@Composable
private fun SeekButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(CircleShape)
            .clickable(onClickLabel = contentDescription, onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = OverlayContentColour,
            modifier = Modifier.size(SeekIconSize)
        )
    }
}

@Composable
private fun OverlayIconButton(
    painter: Painter,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    iconSize: Dp = SeekIconSize
) {
    IconButton(onClick = onClick, modifier = modifier) {
        Icon(
            painter = painter,
            contentDescription = contentDescription,
            tint = OverlayContentColour,
            modifier = Modifier.size(iconSize)
        )
    }
}

// Controls sit on video and on a black letterbox, never on a themed surface, so they keep a fixed
// light colour rather than following the colour scheme into invisibility.
private val OverlayContentColour = Color.White

private const val PREVIEW_POSITION_MILLIS = 7_000L
private const val PREVIEW_DURATION_MILLIS = 41_000L
private const val PREVIEW_ASPECT_RATIO = 16f / 9f

/**
 * Stands in for a player wherever one cannot exist — the preview renderer.
 *
 * This is what the seam being an interface buys: the controls can be laid out and reviewed without a
 * platform player behind them.
 */
private class PreviewVideoPlayerState(
    override val isPlaying: Boolean = true,
    override val positionMillis: Long = PREVIEW_POSITION_MILLIS,
    override val durationMillis: Long = PREVIEW_DURATION_MILLIS,
    initiallyMuted: Boolean = true
) : VideoPlayerState {

    override val aspectRatio = PREVIEW_ASPECT_RATIO

    override var isMuted = initiallyMuted

    override fun play() = Unit

    override fun pause() = Unit

    override fun seekTo(millis: Long) = Unit

    override suspend fun trackTimeline() = Unit

    @Composable
    override fun Surface(modifier: Modifier) {
        Box(modifier = modifier.background(MaterialTheme.colorScheme.surfaceVariant))
    }
}

@Preview
@Composable
private fun VideoCardPreview() {
    DeepskyTheme {
        VideoCard(
            state = PreviewVideoPlayerState(),
            onEnterFullscreen = {},
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Preview
@Composable
private fun FullscreenVideoPlayingPreview() {
    DeepskyTheme {
        FullscreenVideoContent(
            state = PreviewVideoPlayerState(),
            onTogglePlayback = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun FullscreenVideoPausedPreview() {
    DeepskyTheme {
        FullscreenVideoContent(
            state = PreviewVideoPlayerState(isPlaying = false),
            onTogglePlayback = {},
            onDismiss = {}
        )
    }
}

@Preview
@Composable
private fun FullscreenVideoUnmutedPreview() {
    DeepskyTheme {
        FullscreenVideoContent(
            state = PreviewVideoPlayerState(initiallyMuted = false),
            onTogglePlayback = {},
            onDismiss = {}
        )
    }
}

// The case the NaN guard exists for: no duration yet, so the readout has no total, the bar sits at its
// start and dragging is disabled.
@Preview
@Composable
private fun FullscreenVideoUnknownDurationPreview() {
    DeepskyTheme {
        FullscreenVideoContent(
            state = PreviewVideoPlayerState(positionMillis = 0L, durationMillis = 0L),
            onTogglePlayback = {},
            onDismiss = {}
        )
    }
}

// A clip long enough for the readout to grow an hour segment, which changes the format.
@Preview
@Composable
private fun FullscreenVideoLongClipPreview() {
    DeepskyTheme {
        FullscreenVideoContent(
            state = PreviewVideoPlayerState(positionMillis = 3_723_000L, durationMillis = 7_265_000L),
            onTogglePlayback = {},
            onDismiss = {}
        )
    }
}
