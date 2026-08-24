package com.wojciechkula.deepskyapp.core.media

import android.graphics.Bitmap
import android.media.MediaMetadataRetriever
import android.os.Build
import coil3.Image
import coil3.asImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class AndroidVideoFrameLoader : VideoFrameLoader {

    override suspend fun loadFrame(
        url: String,
        maxWidth: Int,
        maxHeight: Int
    ): Image? = withContext(Dispatchers.IO) {
        val retriever = MediaMetadataRetriever()
        try {
            // The URL overload reads over HTTP in ranges, so only the container header and the frames
            // near the requested time cross the network rather than the whole clip.
            retriever.setDataSource(url, emptyMap())
            retriever.scaledOrFullFrame(maxWidth, maxHeight)?.asImage()
        } catch (ignored: RuntimeException) {
            null
        } finally {
            retriever.release()
        }
    }

    // getScaledFrameAtTime arrived in API 27 and this module's minSdk is 26, so the unscaled frame is
    // the floor rather than the choice.
    private fun MediaMetadataRetriever.scaledOrFullFrame(
        maxWidth: Int,
        maxHeight: Int
    ): Bitmap? = if (maxWidth > 0 && maxHeight > 0 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        getScaledFrameAtTime(0, MediaMetadataRetriever.OPTION_CLOSEST_SYNC, maxWidth, maxHeight)
    } else {
        frameAtTime
    }
}

actual fun videoFrameLoader(): VideoFrameLoader = AndroidVideoFrameLoader()
