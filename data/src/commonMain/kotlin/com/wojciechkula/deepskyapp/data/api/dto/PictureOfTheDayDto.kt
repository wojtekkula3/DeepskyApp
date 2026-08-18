package com.wojciechkula.deepskyapp.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
internal data class PictureOfTheDayDto(
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    // APOD omits `hdurl` for video media types, so it is optional here.
    @SerialName("hdurl") val hdUrl: String = "",
    @SerialName("media_type") val mediaType: String,
    @SerialName("service_version") val serviceVersion: String,
    val title: String,
    val url: String
)
