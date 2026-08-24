package com.wojciechkula.deepskyapp.core.media

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.UIKitView
import kotlin.coroutines.resume
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.readValue
import kotlinx.cinterop.useContents
import kotlinx.coroutines.delay
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFAudio.AVAudioSession
import platform.AVFAudio.AVAudioSessionCategoryPlayback
import platform.AVFAudio.AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation
import platform.AVFAudio.setActive
import platform.AVFoundation.AVAssetTrack
import platform.AVFoundation.AVLayerVideoGravityResizeAspect
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.AVPlayer
import platform.AVFoundation.AVPlayerItem
import platform.AVFoundation.AVPlayerItemDidPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemFailedToPlayToEndTimeNotification
import platform.AVFoundation.AVPlayerItemStatusFailed
import platform.AVFoundation.AVPlayerItemStatusReadyToPlay
import platform.AVFoundation.AVPlayerLayer
import platform.AVFoundation.AVPlayerTimeControlStatusPaused
import platform.AVFoundation.AVURLAsset
import platform.AVFoundation.currentItem
import platform.AVFoundation.currentTime
import platform.AVFoundation.duration
import platform.AVFoundation.loadTracksWithMediaType
import platform.AVFoundation.muted
import platform.AVFoundation.naturalSize
import platform.AVFoundation.pause
import platform.AVFoundation.play
import platform.AVFoundation.seekToTime
import platform.AVFoundation.timeControlStatus
import platform.CoreGraphics.CGRectZero
import platform.CoreMedia.CMTimeGetSeconds
import platform.CoreMedia.CMTimeMake
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.CoreMedia.kCMTimeZero
import platform.Foundation.NSNotificationCenter
import platform.Foundation.NSOperationQueue
import platform.Foundation.NSURL
import platform.UIKit.UIView

private const val STATUS_POLL_INTERVAL_MILLIS = 200L
private const val POSITION_POLL_INTERVAL_MILLIS = 250L
private const val MILLIS_PER_SECOND = 1000
private const val SEEK_TIMESCALE = 1000

@OptIn(ExperimentalForeignApi::class)
@Composable
actual fun rememberVideoPlayerState(
    url: String,
    onError: () -> Unit
): VideoPlayerState {
    // So the observers built once below always call the current lambda, as on Android.
    val currentOnError by rememberUpdatedState(onError)
    val assetUrl = remember(url) { NSURL.URLWithString(url) }

    if (assetUrl == null) {
        LaunchedEffect(url) { currentOnError() }
        return remember { UnplayableVideoPlayerState() }
    }

    val asset = remember(assetUrl) { AVURLAsset(uRL = assetUrl, options = null) }
    val state = remember(asset) { AvPlayerState(asset) { currentOnError() } }

    LaunchedEffect(state) {
        // Track loading is asynchronous for a remote asset, so the real ratio arrives after the first
        // frames and the widescreen default holds until then. Awaiting the size rather than assigning
        // it from the completion handler keeps the state write on the composition's dispatcher.
        asset.awaitVideoAspectRatio()?.let { state.aspectRatio = it }
    }

    LaunchedEffect(state) {
        if (state.awaitInitialFailure()) state.reportError()
    }

    DisposableEffect(state) {
        onDispose { state.release() }
    }

    return state
}

@OptIn(ExperimentalForeignApi::class)
private class AvPlayerState(
    asset: AVURLAsset,
    private val onError: () -> Unit
) : VideoPlayerState {

    private var errorReported = false

    private val player = AVPlayer.playerWithPlayerItem(AVPlayerItem.playerItemWithAsset(asset)).apply {
        muted = true
    }

    private val loopObserver = NSNotificationCenter.defaultCenter.addObserverForName(
        name = AVPlayerItemDidPlayToEndTimeNotification,
        `object` = player.currentItem,
        queue = NSOperationQueue.mainQueue
    ) { _ ->
        // Read the player rather than the cached flag: the notification can land just after playback
        // was paused or interrupted, and a rewind must not resume it.
        val resume = isTryingToPlay()
        player.seekToTime(CMTimeMake(value = 0, timescale = 1))
        if (resume) player.play()
        syncPlaybackState()
    }

    // Covers a clip that fails partway through; one that never starts is caught by [awaitInitialFailure].
    private val failureObserver = NSNotificationCenter.defaultCenter.addObserverForName(
        name = AVPlayerItemFailedToPlayToEndTimeNotification,
        `object` = player.currentItem,
        queue = NSOperationQueue.mainQueue
    ) { _ ->
        reportError()
    }

    override var aspectRatio by mutableFloatStateOf(DEFAULT_ASPECT_RATIO)

    // Mirrors the player rather than only remembering what it was last told, so an interruption or a
    // lost audio session is reflected — the same meaning `playWhenReady` gives this on Android.
    override var isPlaying by mutableStateOf(false)
        private set

    private var mutedState by mutableStateOf(true)

    private val audioSession = PlaybackAudioSession()

    override var isMuted: Boolean
        get() = mutedState
        set(value) {
            mutedState = value
            player.muted = value
            // Claiming the session before anything is audible would silence other apps for a muted
            // card, so it is taken on the first unmute — and handed straight back on muting, or other
            // apps stay suppressed after the user has left the video.
            if (value) audioSession.release() else audioSession.take()
        }

    override var positionMillis by mutableLongStateOf(0L)
        private set

    override var durationMillis by mutableLongStateOf(0L)
        private set

    private var pendingSeek: PendingSeek? = null

    override fun play() {
        player.play()
        syncPlaybackState()
    }

    override fun pause() {
        player.pause()
        syncPlaybackState()
    }

    override fun seekTo(millis: Long) {
        pendingSeek = PendingSeek(target = millis, from = reportedPositionMillis())
        positionMillis = millis
        // Zero tolerance so a five-second step lands where it says it does instead of on the nearest
        // keyframe; these clips are short enough for the extra decoding not to show.
        player.seekToTime(
            time = CMTimeMakeWithSeconds(millis / MILLIS_PER_SECOND.toDouble(), SEEK_TIMESCALE),
            toleranceBefore = kCMTimeZero.readValue(),
            toleranceAfter = kCMTimeZero.readValue()
        )
    }

    override suspend fun trackTimeline() {
        while (true) {
            syncTimeline()
            syncPlaybackState()
            delay(POSITION_POLL_INTERVAL_MILLIS)
        }
    }

    private fun reportedPositionMillis(): Long =
        CMTimeGetSeconds(player.currentTime()).secondsToMillisOrZero()

    private fun syncTimeline() {
        val reported = reportedPositionMillis()
        val pending = pendingSeek
        if (pending == null) {
            positionMillis = reported
        } else if (hasSeekSettled(reported, pending.target, pending.from)) {
            pendingSeek = null
            positionMillis = reported
        }
        durationMillis = player.currentItem?.duration
            ?.let { CMTimeGetSeconds(it) }
            .secondsToMillisOrZero()
    }

    private fun syncPlaybackState() {
        isPlaying = isTryingToPlay()
    }

    // Waiting to play still counts as playing, so buffering does not flip a pause control to play.
    private fun isTryingToPlay(): Boolean = player.timeControlStatus != AVPlayerTimeControlStatusPaused

    /**
     * Suspends until the item either fails to load or becomes ready, and reports which.
     *
     * The item's status is the only signal for a clip that never starts — a 404, an unreachable host or
     * an undecodable codec post no notification at all. It is polled rather than observed because
     * Kotlin/Native cannot override `observeValueForKeyPath`, so KVO has nowhere to deliver. Polling
     * stops at either outcome; a failure once playing is what [failureObserver] is for.
     */
    suspend fun awaitInitialFailure(): Boolean {
        while (true) {
            when (player.currentItem?.status) {
                AVPlayerItemStatusFailed -> return true
                AVPlayerItemStatusReadyToPlay -> return false
                else -> delay(STATUS_POLL_INTERVAL_MILLIS)
            }
        }
    }

    // Both failure paths can describe the same dead clip, and the caller only needs telling once.
    // Safe without synchronisation because every caller is on the main queue.
    fun reportError() {
        if (errorReported) return
        errorReported = true
        onError()
    }

    fun release() {
        isPlaying = false
        player.pause()
        audioSession.release()
        NSNotificationCenter.defaultCenter.removeObserver(loopObserver)
        NSNotificationCenter.defaultCenter.removeObserver(failureObserver)
    }

    @Composable
    override fun Surface(modifier: Modifier) {
        // UIKitView never re-runs its factory, so without this the container would keep showing the
        // previous clip once a new state — and with it a new player — replaced this one.
        key(this) {
            UIKitView(
                factory = { PlayerLayerView(player) },
                modifier = modifier,
                onRelease = { it.detach() }
            )
        }
    }
}

/**
 * Holds the process-wide playback audio session for as long as one player needs to be audible.
 *
 * Taking it is what makes sound survive the ringer switch; giving it back is what lets a music app the
 * user had paused resume, which is why muting hands it over rather than only silencing the player.
 * Tracks whether it took the session, so it can never deactivate one another player is using.
 */
@OptIn(ExperimentalForeignApi::class)
private class PlaybackAudioSession {

    private var held = false

    fun take() {
        if (held) return
        held = true
        val session = AVAudioSession.sharedInstance()
        session.setCategory(AVAudioSessionCategoryPlayback, null)
        session.setActive(true, null)
    }

    fun release() {
        if (!held) return
        held = false
        AVAudioSession.sharedInstance().setActive(
            false,
            AVAudioSessionSetActiveOptionNotifyOthersOnDeactivation,
            null
        )
    }
}

/** Draws one [AVPlayer]'s frames. The player outlives this view, so releasing it here would be wrong. */
@OptIn(ExperimentalForeignApi::class)
private class PlayerLayerView(player: AVPlayer) : UIView(frame = CGRectZero.readValue()) {

    private val playerLayer = AVPlayerLayer.playerLayerWithPlayer(player).apply {
        videoGravity = AVLayerVideoGravityResizeAspect
    }

    init {
        layer.addSublayer(playerLayer)
    }

    // A CALayer has no autolayout of its own and must be resized by hand.
    override fun layoutSubviews() {
        super.layoutSubviews()
        playerLayer.setFrame(bounds)
    }

    fun detach() {
        playerLayer.removeFromSuperlayer()
    }
}

/**
 * Stands in for a URL the platform cannot parse, so the caller still gets a state to hold.
 *
 * A class rather than an object: an object's `isMuted` would be process-wide mutable state shared by
 * every unparseable URL.
 */
private class UnplayableVideoPlayerState : VideoPlayerState {
    override val aspectRatio = DEFAULT_ASPECT_RATIO
    override val isPlaying = false
    override val positionMillis = 0L
    override val durationMillis = 0L

    override var isMuted by mutableStateOf(true)

    override fun play() = Unit

    override fun pause() = Unit

    override fun seekTo(millis: Long) = Unit

    override suspend fun trackTimeline() = Unit

    @Composable
    override fun Surface(modifier: Modifier) = Unit
}

private fun Double?.secondsToMillisOrZero(): Long =
    if (this == null || !isFinite() || this <= 0.0) 0L else (this * MILLIS_PER_SECOND).toLong()

@OptIn(ExperimentalForeignApi::class)
private suspend fun AVURLAsset.awaitVideoAspectRatio(): Float? = suspendCancellableCoroutine { continuation ->
    loadTracksWithMediaType(AVMediaTypeVideo) { tracks, _ ->
        val track = tracks?.firstOrNull() as? AVAssetTrack
        continuation.resume(track?.naturalSize?.useContents { aspectRatioOrNull(width, height) })
    }
}
