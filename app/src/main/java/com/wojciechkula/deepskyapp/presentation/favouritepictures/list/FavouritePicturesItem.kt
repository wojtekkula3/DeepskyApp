package com.wojciechkula.deepskyapp.presentation.favouritepictures.list

import android.graphics.Bitmap

data class FavouritePicturesItem(
    val id: Int,
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    val title: String,
    val url: String,
    val bitmap: Bitmap
)