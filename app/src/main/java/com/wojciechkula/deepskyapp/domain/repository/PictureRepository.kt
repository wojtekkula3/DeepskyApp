package com.wojciechkula.deepskyapp.domain.repository

import androidx.lifecycle.LiveData
import com.wojciechkula.deepskyapp.data.api.ApiResult
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import retrofit2.Response

interface PictureRepository {

    suspend fun getPictureOfTheDay(): ApiResult<PictureOfTheDayModel>
    fun getFavouritePictures(): LiveData<List<FavouritePictureModel>>
    fun getPictureByDate(date: String): LiveData<List<FavouritePictureModel>>
    suspend fun addFavouritePicture(picture: FavouritePictureModel): Long
    suspend fun deleteFavouritePicture(date: String): Int
}