package com.wojciechkula.deepskyapp.presentation.favouritepictures.list

data class FavouritePicturesItem(
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    val title: String,
    val url: String
)