package com.wojciechkula.deepskyapp.domain.model

data class PictureOfTheDayModel(
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    val hdurl: String,
    val media_type: String,
    val service_version: String,
    val title: String,
    val url: String
)