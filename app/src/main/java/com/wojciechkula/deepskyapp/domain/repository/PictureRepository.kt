package com.wojciechkula.deepskyapp.domain.repository

import androidx.lifecycle.LiveData
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import retrofit2.Response

interface PictureRepository {

    suspend fun getPictureOfTheDay(): Response<PictureOfTheDayModel>
    fun getFavouritePictures(): LiveData<List<FavouritePictureModel>>
    suspend fun getPictureByDate(date: String): List<FavouritePictureModel>
    suspend fun addFavouritePicture(picture: FavouritePictureModel): Long
    suspend fun deleteFavouritePicture(date: String): Int
}