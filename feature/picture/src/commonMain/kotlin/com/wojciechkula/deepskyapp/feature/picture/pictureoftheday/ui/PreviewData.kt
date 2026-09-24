package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui

import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel

internal val previewPicture = PictureOfTheDayModel(
    copyright = "NASA",
    date = "2026-07-12",
    explanation = "A demo nebula rendered in the preview, showcasing dynamic cosmic dust clouds and deep space starlight effects.",
    hdUrl = "",
    mediaType = "image",
    serviceVersion = "v1",
    title = "Preview Nebula",
    url = ""
)

// An empty url has no recognised extension, so this classifies as an embed with no thumbnail.
internal val previewVideo = previewPicture.copy(
    mediaType = "video"
)

internal val previewVideoFile = previewPicture.copy(
    mediaType = "video",
    url = "https://apod.nasa.gov/apod/image/2608/eso2612b.mp4",
    title = "Preview Clip"
)
