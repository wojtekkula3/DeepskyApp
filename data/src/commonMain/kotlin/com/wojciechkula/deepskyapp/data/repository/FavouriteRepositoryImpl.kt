package com.wojciechkula.deepskyapp.data.repository

import com.wojciechkula.deepskyapp.data.database.dao.FavouritePictureDao
import com.wojciechkula.deepskyapp.data.mapper.toDomain
import com.wojciechkula.deepskyapp.data.mapper.toEntity
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

internal class FavouriteRepositoryImpl(
    private val dao: FavouritePictureDao,
) : FavouriteRepository {

    override fun getFavouritePictures(): Flow<List<FavouritePictureModel>> =
        dao.getFavouritePictures().map { entities -> entities.map { it.toDomain() } }

    override fun isFavourite(date: String): Flow<Boolean> =
        dao.getByDate(date).map { it.isNotEmpty() }

    override suspend fun addFavouritePicture(picture: FavouritePictureModel): Long =
        dao.add(picture.toEntity())

    override suspend fun deleteFavouritePicture(date: String): Int =
        dao.delete(date)
}
