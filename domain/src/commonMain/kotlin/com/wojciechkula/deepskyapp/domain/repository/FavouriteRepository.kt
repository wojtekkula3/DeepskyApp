package com.wojciechkula.deepskyapp.domain.repository

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import kotlinx.coroutines.flow.Flow

interface FavouriteRepository {
    fun getFavouritePictures(): Flow<List<FavouritePictureModel>>
    fun isFavourite(date: String): Flow<Boolean>
    suspend fun addFavouritePicture(picture: FavouritePictureModel): Long
    suspend fun deleteFavouritePicture(date: String): Int
}
