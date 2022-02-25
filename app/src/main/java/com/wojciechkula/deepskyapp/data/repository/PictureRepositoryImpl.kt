package com.wojciechkula.deepskyapp.data.repository

import androidx.lifecycle.map
import com.wojciechkula.deepskyapp.data.api.APODApi
import com.wojciechkula.deepskyapp.data.datasource.LocalDataSource
import com.wojciechkula.deepskyapp.data.entity.FavouritePictureEntity
import com.wojciechkula.deepskyapp.data.mapper.FavouritePictureMapper
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import retrofit2.Response
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val api: APODApi,
    private val localDataSource: LocalDataSource,
    private val favPicMapper: FavouritePictureMapper
) : PictureRepository {

    override suspend fun getPictureOfTheDay(): Response<PictureOfTheDayModel> = api.getPictureOfTheDay()

    override fun getFavouritePictures() =
        localDataSource.getFavouritePictures().map { list: List<FavouritePictureEntity> ->
            list.map { favouritePictureEntity ->
                favPicMapper.mapToDomain(favouritePictureEntity)
            }
        }

    override suspend fun getPictureByDate(date: String) = localDataSource.getPictureByDate(date)
        .map { favouritePictureEntity -> favPicMapper.mapToDomain(favouritePictureEntity) }

    override suspend fun addFavouritePicture(picture: FavouritePictureModel): Long =
        localDataSource.saveTodayPicture(favPicMapper.mapToEntity(picture))

    override suspend fun deleteFavouritePicture(date: String): Int =
        localDataSource.deleteFavouritePicture(date)
}