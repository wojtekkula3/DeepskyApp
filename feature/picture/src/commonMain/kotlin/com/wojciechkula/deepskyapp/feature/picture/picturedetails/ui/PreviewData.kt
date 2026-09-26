package com.wojciechkula.deepskyapp.feature.picture.picturedetails.ui

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel

internal val previewPicture = FavouritePictureModel(
    id = 1L,
    copyright = "NASA",
    date = "2026-07-10",
    explanation = "A saved favourite rendered in the preview, showcasing dynamic cosmic dust clouds and deep space starlight effects.",
    hdUrl = "",
    mediaType = "image",
    serviceVersion = "v1",
    title = "Preview Favourite",
    url = ""
)

internal val previewVideoFile = previewPicture.copy(
    mediaType = "video",
    url = "https://apod.nasa.gov/apod/image/2608/eso2612b.mp4",
    title = "Preview Clip"
)
