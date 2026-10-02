package com.wojciechkula.deepskyapp.data.repository

import com.wojciechkula.deepskyapp.data.database.dao.FavouritePictureDao
import com.wojciechkula.deepskyapp.data.mapper.APOD_BASIC_SERVICE_VERSION
import com.wojciechkula.deepskyapp.data.mapper.toDomain
import com.wojciechkula.deepskyapp.data.mapper.toEntity
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FavouriteRepositoryImpl(private val dao: FavouritePictureDao) : FavouriteRepository {

    override fun getFavouritePictures(): Flow<List<FavouritePictureModel>> =
        dao.getFavouritePictures().map { entities -> entities.map { it.toDomain() } }

    override fun isFavourite(date: String): Flow<Boolean> =
        dao.getByDate(date).map { it.isNotEmpty() }

    override suspend fun addFavouritePicture(picture: FavouritePictureModel): Long =
        dao.add(picture.toEntity())

    override suspend fun deleteFavouritePicture(date: String): Int =
        dao.delete(date)

    // The legacy API's rows (migrated from 1.2.1) carry its service_version, "v1".
    override suspend fun getLegacyFavouritePictures(): List<FavouritePictureModel> =
        dao.getSavedFromOtherService(APOD_BASIC_SERVICE_VERSION).map { it.toDomain() }

    override suspend fun updateFavouritePicture(picture: FavouritePictureModel): Int =
        dao.update(picture.toEntity())
}
