package com.wojciechkula.deepskyapp.data.mapper

import com.wojciechkula.deepskyapp.data.api.dto.ApodBasicDto
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel

private const val IMAGE = "image"
private const val VIDEO = "video"
private const val UNSUPPORTED = "unsupported"
internal const val APOD_BASIC_SERVICE_VERSION = "apod-basic"
private const val CARD_IMAGE_WIDTH = 1280

// Coil's default maxBitmapSize: anything larger is downloaded only to be scaled down on decode.
private const val FULL_SCREEN_IMAGE_SIZE = 4096

// Favourites persist this model, so `url` must stay the media itself and text must stay renderable
// the way rows saved from the legacy API are.
internal fun ApodBasicDto.toDomain(): PictureOfTheDayModel {
    val hdUrl = hdUrl.orEmpty()
    val videoUrl = if (mediaType == VIDEO || mediaType == "iframe") basicHtml?.firstVideoSource() else null
    return PictureOfTheDayModel(
        copyright = copyright.orEmpty().htmlToPlainText().withoutCreditLabel().ifBlank { null },
        date = date,
        explanation = explanation.orEmpty().withoutExplanationLabel().withoutSiteNotices(),
        hdUrl = hdUrl.withParameterAtMost("w", FULL_SCREEN_IMAGE_SIZE).withParameterAtMost("h", FULL_SCREEN_IMAGE_SIZE),
        mediaType = when {
            mediaType == IMAGE -> IMAGE
            videoUrl != null -> VIDEO
            else -> UNSUPPORTED
        },
        serviceVersion = APOD_BASIC_SERVICE_VERSION,
        thumbnailUrl = videoUrl?.youTubeThumbnailUrl(),
        title = title.orEmpty().htmlToPlainText(),
        url = videoUrl ?: hdUrl.withParameterAtMost("w", CARD_IMAGE_WIDTH)
    )
}

// The asset CDN fits the image into the w×h box keeping its aspect ratio, so lowering either bound scales it.
private fun String.withParameterAtMost(name: String, max: Int): String =
    replace(Regex("([?&]$name=)(\\d+)")) { match ->
        if ((match.groupValues[2].toIntOrNull() ?: Int.MAX_VALUE) > max) "${match.groupValues[1]}$max" else match.value
    }
