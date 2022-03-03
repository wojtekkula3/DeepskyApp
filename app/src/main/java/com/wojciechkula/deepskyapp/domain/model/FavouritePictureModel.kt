package com.wojciechkula.deepskyapp.domain.model

import android.graphics.Bitmap

data class FavouritePictureModel (
    val id: Int? = null,
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    val hdurl: String,
    val media_type: String,
    val service_version: String,
    val title: String,
    val url: String,
    val bitmap: Bitmap,
)