package com.wojciechkula.deepskyapp.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favourite_pictures")
internal data class FavouritePictureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    val hdUrl: String,
    val mediaType: String,
    val serviceVersion: String,
    val title: String,
    val url: String,
)
