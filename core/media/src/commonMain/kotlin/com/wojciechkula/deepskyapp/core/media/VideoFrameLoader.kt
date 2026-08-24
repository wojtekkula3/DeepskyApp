package com.wojciechkula.deepskyapp.core.media

import coil3.Image

/**
 * Reads a still frame out of a video so it can stand in as a thumbnail.
 *
 * An interface rather than an `expect fun` so the behaviour can be substituted; [videoFrameLoader] is
 * the pure factory that builds the platform implementation.
 */
interface VideoFrameLoader {

    /**
     * Returns the first frame of the video at [url], scaled to fit within [maxWidth] by [maxHeight]
     * when both are positive and returned at full size otherwise, or `null` if it cannot be read — an
     * unreachable URL, an unsupported container, or a read that took too long.
     */
    suspend fun loadFrame(
        url: String,
        maxWidth: Int,
        maxHeight: Int
    ): Image?
}

/** Returns the platform frame loader: `MediaMetadataRetriever` on Android, `AVFoundation` on iOS. */
expect fun videoFrameLoader(): VideoFrameLoader
