package com.wojciechkula.deepskyapp.core.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.compose.PlayerSurface
import androidx.media3.ui.compose.SURFACE_TYPE_TEXTURE_VIEW
import kotlinx.coroutines.delay

private const val POSITION_POLL_INTERVAL_MILLIS = 250L

// Audio focus must not be requested by a silent card, so it is left to setMuted to switch on.
private val MovieAudioAttributes = AudioAttributes.Builder()
    .setUsage(C.USAGE_MEDIA)
    .setContentType(C.AUDIO_CONTENT_TYPE_MOVIE)
    .build()

@Composable
actual fun rememberVideoPlayerState(
    url: String,
    onError: () -> Unit
): VideoPlayerState {
    val context = LocalContext.current
    // So the listener below always calls the current lambda without the effect having to restart.
    val currentOnError by rememberUpdatedState(onError)

    val player = remember(url) {
        ExoPlayer.Builder(context).build().apply {
            setMediaItem(MediaItem.fromUri(url))
            repeatMode = Player.REPEAT_MODE_ONE
            setAudioAttributes(MovieAudioAttributes, false)
            volume = 0f
            prepare()
        }
    }
    val state = remember(player) { ExoPlayerState(player) }

    DisposableEffect(state) {
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                aspectRatioOrNull(videoSize.width.toDouble(), videoSize.height.toDouble())
                    ?.let { state.aspectRatio = it }
            }

            // Losing audio focus makes media3 drop playWhenReady itself, so the pause button has to
            // read the player rather than only remember what it was last told.
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                state.isPlaying = playWhenReady
            }

            override fun onPlayerError(error: PlaybackException) {
                currentOnError()
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    return state
}

private class ExoPlayerState(private val player: ExoPlayer) : VideoPlayerState {

    override var aspectRatio by mutableFloatStateOf(DEFAULT_ASPECT_RATIO)

    override var isPlaying by mutableStateOf(player.playWhenReady)

    private var mutedState by mutableStateOf(true)

    override var isMuted: Boolean
        get() = mutedState
        set(value) {
            mutedState = value
            // Focus is claimed only while audible: media3 requests it from playWhenReady regardless of
            // volume, so a muted looping card would otherwise stop whatever the user is listening to.
            player.setAudioAttributes(MovieAudioAttributes, !value)
            player.volume = if (value) 0f else 1f
        }

    override var positionMillis by mutableLongStateOf(0L)
        private set

    override var durationMillis by mutableLongStateOf(0L)
        private set

    private var pendingSeek: PendingSeek? = null

    override fun play() {
        player.playWhenReady = true
    }

    override fun pause() {
        player.playWhenReady = false
    }

    override fun seekTo(millis: Long) {
        pendingSeek = PendingSeek(target = millis, from = player.currentPosition.coerceAtLeast(0L))
        positionMillis = millis
        player.seekTo(millis)
    }

    override suspend fun trackTimeline() {
        while (true) {
            syncTimeline()
            delay(POSITION_POLL_INTERVAL_MILLIS)
        }
    }

    private fun syncTimeline() {
        val reported = player.currentPosition.coerceAtLeast(0L)
        val pending = pendingSeek
        if (pending == null) {
            positionMillis = reported
        } else if (hasSeekSettled(reported, pending.target, pending.from)) {
            pendingSeek = null
            positionMillis = reported
        }
        // An unprepared or live item reports TIME_UNSET, which is a large negative sentinel.
        durationMillis = player.duration.takeIf { it != C.TIME_UNSET && it > 0L } ?: 0L
    }

    @androidx.annotation.OptIn(markerClass = [UnstableApi::class])
    @Composable
    override fun Surface(modifier: Modifier) {
        // A SurfaceView is composited by the system, so it would ignore the rounded card clipping it
        // and lag the content it scrolls with.
        PlayerSurface(
            player = player,
            modifier = modifier,
            surfaceType = SURFACE_TYPE_TEXTURE_VIEW
        )
    }
}
