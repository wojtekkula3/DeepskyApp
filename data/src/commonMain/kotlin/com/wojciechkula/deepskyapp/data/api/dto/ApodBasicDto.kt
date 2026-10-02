package com.wojciechkula.deepskyapp.data.api.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// `url` is deliberately not mapped: in this API it is the article page, not the media.
@Serializable
internal data class ApodBasicDto(
    val date: String,
    val title: String? = null,
    @SerialName("media_type") val mediaType: String? = null,
    val explanation: String? = null,
    val copyright: String? = null,
    @SerialName("hdurl") val hdUrl: String? = null,
    @SerialName("basic_html") val basicHtml: String? = null
)
