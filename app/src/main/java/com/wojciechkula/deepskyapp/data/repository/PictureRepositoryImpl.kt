package com.wojciechkula.deepskyapp.data.repository

import androidx.lifecycle.map
import com.wojciechkula.deepskyapp.data.api.APODApi
import com.wojciechkula.deepskyapp.data.datasource.LocalDataSource
import com.wojciechkula.deepskyapp.data.entity.FavouritePictureEntity
import com.wojciechkula.deepskyapp.data.mapper.FavouritePictureMapper
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import retrofit2.Response
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val api: APODApi,
    private val localDataSource: LocalDataSource,
    private val mapper: FavouritePictureMapper
) : PictureRepository {

    override suspend fun getPictureOfTheDay(): Response<PictureOfTheDay> = api.getPictureOfTheDay()

    override fun getFavouritePictures() =
        localDataSource.getFavouritePictures().map { list: List<FavouritePictureEntity> ->
            list.map { favouritePictureEntity ->
                mapper.mapToDomain(favouritePictureEntity)
            }
        }

    override suspend fun getPictureByDate(date: String) = localDataSource.getPictureByDate(date)
        .map { favouritePictureEntity -> mapper.mapToDomain(favouritePictureEntity) }

    override suspend fun addFavouritePicture(picture: PictureOfTheDay): Long =
        localDataSource.saveTodayPicture(mapper.mapToEntity(picture))

    override suspend fun deleteFavouritePicture(picture: PictureOfTheDay): Int =
        localDataSource.deleteFavouritePicture(picture.date)
}