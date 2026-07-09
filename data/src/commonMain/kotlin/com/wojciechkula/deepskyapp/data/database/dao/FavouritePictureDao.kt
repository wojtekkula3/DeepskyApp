package com.wojciechkula.deepskyapp.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wojciechkula.deepskyapp.data.database.entity.FavouritePictureEntity
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavouritePictureDao {

    @Query("SELECT * FROM favourite_pictures")
    fun getFavouritePictures(): Flow<List<FavouritePictureEntity>>

    @Query("SELECT * FROM favourite_pictures WHERE date = :date")
    fun getByDate(date: String): Flow<List<FavouritePictureEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(entity: FavouritePictureEntity): Long

    @Query("DELETE FROM favourite_pictures WHERE date = :date")
    suspend fun delete(date: String): Int
}
