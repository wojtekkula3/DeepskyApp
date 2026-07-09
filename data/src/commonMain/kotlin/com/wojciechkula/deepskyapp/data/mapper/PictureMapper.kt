package com.wojciechkula.deepskyapp.data.mapper

import com.wojciechkula.deepskyapp.data.api.dto.PictureOfTheDayDto
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel

internal fun PictureOfTheDayDto.toDomain() = PictureOfTheDayModel(
    copyright = copyright,
    date = date,
    explanation = explanation,
    hdUrl = hdUrl,
    mediaType = mediaType,
    serviceVersion = serviceVersion,
    title = title,
    url = url,
)
