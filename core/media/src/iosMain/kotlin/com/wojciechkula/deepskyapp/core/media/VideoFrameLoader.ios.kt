package com.wojciechkula.deepskyapp.core.media

import coil3.Image
import coil3.asImage
import kotlin.coroutines.resume
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.usePinned
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import org.jetbrains.skia.Bitmap as SkiaBitmap
import org.jetbrains.skia.Image as SkiaImage
import platform.AVFoundation.AVAssetImageGenerator
import platform.AVFoundation.AVURLAsset
import platform.CoreGraphics.CGSizeMake
import platform.CoreMedia.CMTimeMakeWithSeconds
import platform.Foundation.NSData
import platform.Foundation.NSURL
import platform.UIKit.UIImage
import platform.UIKit.UIImagePNGRepresentation
import platform.posix.memcpy

private const val PREFERRED_TIMESCALE = 600
private const val FRAME_TIMEOUT_MILLIS = 20_000L

internal class IosVideoFrameLoader : VideoFrameLoader {

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun loadFrame(
        url: String,
        maxWidth: Int,
        maxHeight: Int
    ): Image? = withContext(Dispatchers.Default) {
        val assetUrl = NSURL.URLWithString(url) ?: return@withContext null
        val generator = AVAssetImageGenerator(AVURLAsset(uRL = assetUrl, options = null)).apply {
            appliesPreferredTrackTransform = true
            if (maxWidth > 0 && maxHeight > 0) {
                maximumSize = CGSizeMake(maxWidth.toDouble(), maxHeight.toDouble())
            }
        }
        val png = withTimeoutOrNull(FRAME_TIMEOUT_MILLIS) { generator.awaitFirstFrameAsPng() }
            ?: return@withContext null
        val decoded = SkiaImage.makeFromEncoded(png.toByteArray())
        try {
            // `coil3.Bitmap` is a typealias for the Skia bitmap type on non-Android targets, which is why
            // the frame takes a PNG detour instead of being handed over as a UIImage.
            SkiaBitmap.makeFromImage(decoded).asImage()
        } finally {
            decoded.close()
        }
    }
}

actual fun videoFrameLoader(): VideoFrameLoader = IosVideoFrameLoader()

/**
 * The asynchronous generator, because the synchronous `copyCGImageAtTime` is deprecated as of iOS 18 and
 * a synchronous read of a slow HTTP asset can reset media services — which would also take down any
 * player running alongside it.
 *
 * The frame is encoded here rather than returned: the completion handler receives it unowned, so it is
 * valid only until the handler returns and must not be released.
 */
@OptIn(ExperimentalForeignApi::class)
private suspend fun AVAssetImageGenerator.awaitFirstFrameAsPng(): NSData? =
    suspendCancellableCoroutine { continuation ->
        continuation.invokeOnCancellation { cancelAllCGImageGeneration() }
        generateCGImageAsynchronouslyForTime(
            requestedTime = CMTimeMakeWithSeconds(0.0, PREFERRED_TIMESCALE)
        ) { frame, _, _ ->
            continuation.resume(frame?.let { UIImagePNGRepresentation(UIImage.imageWithCGImage(it)) })
        }
    }

@OptIn(ExperimentalForeignApi::class)
private fun NSData.toByteArray(): ByteArray {
    val size = length.toInt()
    if (size == 0) return ByteArray(0)
    return ByteArray(size).apply {
        usePinned { pinned -> memcpy(pinned.addressOf(0), bytes, length) }
    }
}
