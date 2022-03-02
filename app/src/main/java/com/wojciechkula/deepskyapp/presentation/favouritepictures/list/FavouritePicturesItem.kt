package com.wojciechkula.deepskyapp.presentation.favouritepictures.list

import android.graphics.Bitmap
import java.util.*

data class FavouritePicturesItem(
    val id: Int,
    val copyright: String? = null,
    val date: Date,
    val explanation: String,
    val title: String,
    val url: String,
    val bitmap: Bitmap
)