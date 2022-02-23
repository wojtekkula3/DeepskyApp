package com.wojciechkula.deepskyapp.domain.repository

import androidx.lifecycle.LiveData
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
import retrofit2.Response

interface PictureRepository {

    suspend fun getPictureOfTheDay(): Response<PictureOfTheDay>
    fun getFavouritePictures(): LiveData<List<PictureOfTheDay>>
    suspend fun getPictureByDate(date: String): List<PictureOfTheDay>
    suspend fun addFavouritePicture(picture: PictureOfTheDay): Long
    suspend fun deleteFavouritePicture(picture: PictureOfTheDay): Int
}