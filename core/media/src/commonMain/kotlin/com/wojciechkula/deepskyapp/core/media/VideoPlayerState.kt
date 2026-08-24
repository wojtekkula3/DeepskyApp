package com.wojciechkula.deepskyapp.core.media

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

internal const val DEFAULT_ASPECT_RATIO = 16f / 9f

internal fun aspectRatioOrNull(width: Double, height: Double): Float? =
    if (width > 0.0 && height > 0.0) (width / height).toFloat() else null

/**
 * A video player that outlives the composable drawing it.
 *
 * The platform player is owned here rather than inside [Surface] so the same playback can be drawn
 * in more than one place — inline and full screen — without restarting. Position lives in the
 * player, so moving the surface cannot lose it.
 *
 * Every observable property is backed by Compose state, so reading one in composition subscribes
 * to it. [durationMillis] is `0` while unknown, which callers must treat as "no duration yet"
 * rather than "an empty clip": a platform time can be indefinite, and normalising it here keeps
 * a NaN out of the caller's layout arithmetic.
 */
interface VideoPlayerState {

    val aspectRatio: Float

    /**
     * Whether the player is *trying* to play: still true while it buffers, and false as soon as
     * anything stops it — a pause, a lost audio focus, an interruption. Both platforms report this,
     * not "frames are moving", so a play/pause control shows what another press would do.
     */
    val isPlaying: Boolean

    /**
     * A property rather than a `setMuted` command on purpose: a property named `isMuted` compiles its
     * setter to `setMuted`, so the two together are a JVM signature clash.
     */
    var isMuted: Boolean

    /** Stale unless something is running [trackTimeline]. */
    val positionMillis: Long

    /** `0` until the platform reports a finite, positive duration, and stale as [positionMillis] is. */
    val durationMillis: Long

    fun play()

    fun pause()

    fun seekTo(millis: Long)

    /**
     * Keeps [positionMillis] and [durationMillis] up to date until cancelled.
     *
     * Reading a position costs a platform call, so it is not tracked by default: an inline card shows
     * no time and would poll for nothing. Only a caller that displays the timeline runs this.
     */
    suspend fun trackTimeline()

    /**
     * Draws the current frames.
     *
     * Only one call site may be composed at a time: both platforms bind a single player to one
     * surface, so a second would take the output away from the first.
     *
     * The given [modifier] is applied as-is — no aspect ratio is imposed, because the caller knows
     * whether it is filling a card or letterboxing a full screen.
     */
    @Composable
    fun Surface(modifier: Modifier)
}

/**
 * Seeks [deltaMillis] from wherever playback currently is, without leaving the clip.
 *
 * A forward seek is refused while the duration is unknown rather than guessed, so the two
 * platforms behave identically at the edges.
 */
fun VideoPlayerState.seekBy(deltaMillis: Long) {
    seekTo(seekTargetMillis(positionMillis, deltaMillis, durationMillis))
}

fun seekTargetMillis(
    positionMillis: Long,
    deltaMillis: Long,
    durationMillis: Long
): Long {
    val target = positionMillis + deltaMillis
    return when {
        target <= 0L -> 0L
        durationMillis <= 0L -> if (deltaMillis > 0L) positionMillis else target
        else -> target.coerceAtMost(durationMillis)
    }
}

/** A seek that has been asked for but not yet observed to land. [from] gives it a direction. */
internal data class PendingSeek(
    val target: Long,
    val from: Long
)

// A seek can land a frame or two off what was asked for, and that still counts as arrived.
internal const val SEEK_SETTLE_TOLERANCE_MILLIS = 400L

/**
 * Whether a player reporting [reportedMillis] has arrived at a seek from [fromMillis] to [targetMillis].
 *
 * A seek is asynchronous, so until it lands the player still reports where it was; showing that would
 * make the readout jump backwards and then forwards again. Implementations therefore hold the requested
 * position until this says the player caught up.
 *
 * Deliberately "at or past the target" rather than "within a window of it": playback keeps moving, so a
 * window can be stepped over between two polls, and a caller waiting for a window it already missed
 * would hold the requested position forever.
 */
internal fun hasSeekSettled(
    reportedMillis: Long,
    targetMillis: Long,
    fromMillis: Long
): Boolean = if (targetMillis >= fromMillis) {
    reportedMillis >= targetMillis - SEEK_SETTLE_TOLERANCE_MILLIS
} else {
    reportedMillis <= targetMillis + SEEK_SETTLE_TOLERANCE_MILLIS
}

/**
 * Creates a player for [url] and releases it when it leaves composition.
 *
 * [onError] fires when the platform gives up on the clip — an unreachable host, or a codec it
 * cannot handle. The caller decides what to show instead; nothing here draws an error state.
 */
@Composable
expect fun rememberVideoPlayerState(
    url: String,
    onError: () -> Unit
): VideoPlayerState
