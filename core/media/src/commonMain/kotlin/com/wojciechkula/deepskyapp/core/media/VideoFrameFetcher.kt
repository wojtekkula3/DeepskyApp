package com.wojciechkula.deepskyapp.core.media

import coil3.ComponentRegistry
import coil3.ImageLoader
import coil3.Uri
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.request.Options
import coil3.size.pxOrElse

// A Fetcher, not a Decoder: a decoder is handed bytes Coil has already downloaded in full, which for an
// APOD clip means pulling ~11 MB to keep one frame. The trade is Coil's disk cache, which a fetcher
// returning a finished image bypasses, so a frame is re-extracted after a cold start.
internal class VideoFrameFetcher(
    private val url: String,
    private val frameLoader: VideoFrameLoader,
    private val maxWidth: Int,
    private val maxHeight: Int
) : Fetcher {

    override suspend fun fetch(): FetchResult {
        // Throwing rather than returning null: Coil reads a null result as "try the next matching
        // fetcher", which is the network one, and that downloads the whole clip. An exception becomes
        // an ErrorResult, so the tile falls back to its error slot.
        val image = frameLoader.loadFrame(url, maxWidth, maxHeight)
            ?: error("No video frame could be read")
        return ImageFetchResult(
            image = image,
            isSampled = maxWidth > 0 && maxHeight > 0,
            dataSource = DataSource.NETWORK
        )
    }

    class Factory(
        private val isVideoUrl: (String) -> Boolean,
        private val frameLoader: VideoFrameLoader
    ) : Fetcher.Factory<Uri> {

        override fun create(data: Uri, options: Options, imageLoader: ImageLoader): Fetcher? =
            fetcherOrNull(
                url = data.toString(),
                maxWidth = options.size.width.pxOrElse { 0 },
                maxHeight = options.size.height.pxOrElse { 0 }
            )

        internal fun fetcherOrNull(
            url: String,
            maxWidth: Int = 0,
            maxHeight: Int = 0
        ): Fetcher? = if (isVideoUrl(url)) {
            VideoFrameFetcher(url, frameLoader, maxWidth, maxHeight)
        } else {
            null
        }
    }
}

/**
 * Registers video-frame thumbnails on this registry.
 *
 * Extends [ComponentRegistry.Builder] rather than `ImageLoader.Builder`: the latter replaces its whole
 * registry on every `components { }` call, so a loader-level helper would be dropped by any later
 * `components { }` — and would drop the components of any earlier one.
 *
 * [isVideoUrl] is a parameter rather than an import so this module stays independent of `:domain`,
 * which owns the single definition of the rule.
 */
fun ComponentRegistry.Builder.addVideoFrameFetcher(
    isVideoUrl: (String) -> Boolean,
    frameLoader: VideoFrameLoader
): ComponentRegistry.Builder = add(VideoFrameFetcher.Factory(isVideoUrl, frameLoader))
