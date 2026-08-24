package com.wojciechkula.deepskyapp.domain.model

data class PictureOfTheDayModel(
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
