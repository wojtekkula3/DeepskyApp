package com.wojciechkula.deepskyapp.domain.model

enum class MediaKind { IMAGE, VIDEO_FILE, VIDEO_EMBED, UNSUPPORTED }

private val VideoFileExtensions = setOf("mp4", "m4v", "mov", "webm")

fun mediaKind(mediaType: String, url: String): MediaKind = when {
    mediaType == "image" -> MediaKind.IMAGE
    mediaType != "video" -> MediaKind.UNSUPPORTED
    isVideoFileUrl(url) -> MediaKind.VIDEO_FILE
    else -> MediaKind.VIDEO_EMBED
}

fun isVideoFileUrl(url: String): Boolean {
    val path = url.substringBefore('?').substringBefore('#')
    return path.substringAfterLast('.', "").lowercase() in VideoFileExtensions
}
