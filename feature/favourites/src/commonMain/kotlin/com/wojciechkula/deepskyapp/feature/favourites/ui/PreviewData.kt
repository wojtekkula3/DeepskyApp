package com.wojciechkula.deepskyapp.feature.favourites.ui

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel

internal val previewImage = FavouritePictureModel(
    id = 1L,
    copyright = null,
    date = "2026-07-10",
    explanation = "e",
    hdUrl = "",
    mediaType = "image",
    serviceVersion = "v1",
    title = "Preview Favourite",
    url = ""
)

// A direct video file: the tile reserves 16:9 for a frame that has to be extracted first.
internal val previewVideoFile = previewImage.copy(
    id = 2L,
    date = "2026-08-21",
    mediaType = "video",
    title = "Preview Clip",
    url = "https://apod.nasa.gov/apod/image/2608/eso2612b.mp4"
)

// An embed: the frame cannot be extracted from a web page, so APOD's own thumbnail stands in.
internal val previewVideoEmbed = previewImage.copy(
    id = 3L,
    date = "2026-08-23",
    mediaType = "video",
    title = "Preview Embed With A Title Long Enough To Wrap",
    url = "https://www.youtube.com/embed/UgxWkOXcdZU",
    thumbnailUrl = "https://img.youtube.com/vi/UgxWkOXcdZU/0.jpg"
)

internal val previewPictures = listOf(previewImage, previewVideoFile, previewVideoEmbed)
