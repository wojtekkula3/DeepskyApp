package com.wojciechkula.deepskyapp.data.mapper

import com.wojciechkula.deepskyapp.data.database.entity.FavouritePictureEntity
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel

internal fun FavouritePictureModel.toEntity() = FavouritePictureEntity(
    id = id ?: 0,
    copyright = copyright,
    date = date,
    explanation = explanation,
    hdUrl = hdUrl,
    mediaType = mediaType,
    serviceVersion = serviceVersion,
    title = title,
    url = url
)

internal fun FavouritePictureEntity.toDomain() = FavouritePictureModel(
    id = id,
    copyright = copyright,
    date = date,
    explanation = explanation,
    hdUrl = hdUrl,
    mediaType = mediaType,
    serviceVersion = serviceVersion,
    title = title,
    url = url
)
