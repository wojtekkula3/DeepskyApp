package com.wojciechkula.deepskyapp.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.wojciechkula.deepskyapp.data.entity.FavouritePictureEntity

@Dao
interface FavouritePictureDao {

    @Query("SELECT * FROM `favourite pictures`")
    fun getFavouritePictues(): LiveData<List<FavouritePictureEntity>>

    @Query("SELECT * FROM `favourite pictures` WHERE date LIKE :date ")
    suspend fun getPictureByDate(date: String): List<FavouritePictureEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavouritePicture(favouritePictureEntity: FavouritePictureEntity): Long

    @Query("DELETE FROM `favourite pictures` WHERE date = :date")
    suspend fun deleteFavouritePicture(date: String): Int
}