package com.wojciechkula.deepskyapp.feature.favourites

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeFavouriteRepository : FavouriteRepository {
    val stored = MutableStateFlow<List<FavouritePictureModel>>(emptyList())
    override fun getFavouritePictures(): Flow<List<FavouritePictureModel>> = stored
    override fun isFavourite(date: String): Flow<Boolean> = MutableStateFlow(false)
    override suspend fun addFavouritePicture(picture: FavouritePictureModel): Long = 1L
    override suspend fun deleteFavouritePicture(date: String): Int = 1
}
