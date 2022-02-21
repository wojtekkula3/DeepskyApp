package com.wojciechkula.deepskyapp.data.entity

data class PictureOfTheDayEntity(
    val id: Int,
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    val hdurl: String,
    val media_type: String,
    val service_version: String,
    val title: String,
    val url: String
)