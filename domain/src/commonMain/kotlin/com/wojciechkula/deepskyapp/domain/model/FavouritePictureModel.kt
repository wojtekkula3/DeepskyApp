package com.wojciechkula.deepskyapp.domain.model

data class FavouritePictureModel(
    val id: Long? = null,
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    val hdUrl: String,
    val mediaType: String,
    val serviceVersion: String,
    val thumbnailUrl: String? = null,
    val title: String,
    val url: String
)

internal fun PictureOfTheDayModel.toFavourite(id: Long? = null) = FavouritePictureModel(
    id = id,
    copyright = copyright,
    date = date,
    explanation = explanation,
    hdUrl = hdUrl,
    mediaType = mediaType,
    serviceVersion = serviceVersion,
    thumbnailUrl = thumbnailUrl,
    title = title,
    url = url
)
