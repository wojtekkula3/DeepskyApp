package com.wojciechkula.deepskyapp.presentation.favouritepictures.list

import android.graphics.Bitmap
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class FavouritePicturesItem(
    val id: Int,
    val copyright: String? = null,
    val date: Date,
    val explanation: String,
    val title: String,
    val url: String,
    val bitmap: Bitmap
) : Parcelable