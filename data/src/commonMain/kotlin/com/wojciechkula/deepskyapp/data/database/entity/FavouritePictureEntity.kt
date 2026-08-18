package com.wojciechkula.deepskyapp.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// The unique index on `date` is what gives OnConflictStrategy.REPLACE a conflict to resolve, so the
// insert behaves as an upsert and a picture cannot be favourited twice.
@Entity(
    tableName = "favourite_pictures",
    indices = [Index(value = ["date"], unique = true)]
)
internal data class FavouritePictureEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val copyright: String? = null,
    val date: String,
    val explanation: String,
    val hdUrl: String,
    val mediaType: String,
    val serviceVersion: String,
    val title: String,
    val url: String
)
