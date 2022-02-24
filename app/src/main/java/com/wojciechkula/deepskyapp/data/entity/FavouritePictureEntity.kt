package com.wojciechkula.deepskyapp.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Favourite pictures")
data class FavouritePictureEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    val hdurl: String,
    val media_type: String,
    val service_version: String,
    val title: String,
    val url: String
)