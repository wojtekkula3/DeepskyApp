package com.wojciechkula.deepskyapp.feature.picture

import com.wojciechkula.deepskyapp.core.common.Analytics

private const val PARAM_SOURCE = "source"

internal fun Analytics.logFavouriteRemoved(source: String) {
    logEvent("favourite_removed", mapOf(PARAM_SOURCE to source))
}

internal fun Analytics.logMediaOpenClicked(
    source: String,
    media: OpenedMedia
) {
    when (media) {
        OpenedMedia.PICTURE -> logEvent("picture_zoom_clicked", mapOf(PARAM_SOURCE to source))
        OpenedMedia.VIDEO_FILE -> logVideoOpenClicked(source, videoType = "file")
        OpenedMedia.VIDEO_EMBED -> logVideoOpenClicked(source, videoType = "embed")
    }
}

private fun Analytics.logVideoOpenClicked(
    source: String,
    videoType: String
) {
    logEvent("video_open_clicked", mapOf(PARAM_SOURCE to source, "video_type" to videoType))
}
