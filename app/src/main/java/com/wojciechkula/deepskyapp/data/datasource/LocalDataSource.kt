package com.wojciechkula.deepskyapp.data.datasource

import com.wojciechkula.deepskyapp.data.dao.FavouritePictureDao
import com.wojciechkula.deepskyapp.data.entity.FavouritePictureEntity
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val favouritePictureDao: FavouritePictureDao,
) {

    fun getFavouritePictures() = favouritePictureDao.getFavouritePictues()

    suspend fun getPictureByDate(date: String) = favouritePictureDao.getPictureByDate(date)

    suspend fun saveTodayPicture(selectedPicture: FavouritePictureEntity) =
        favouritePictureDao.addFavouritePicture(selectedPicture)

    suspend fun deleteFavouritePicture(date: String) =
        favouritePictureDao.deleteFavouritePicture(date)
}