package com.wojciechkula.deepskyapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.wojciechkula.deepskyapp.data.api.APODApi
import com.wojciechkula.deepskyapp.data.api.ApiError
import com.wojciechkula.deepskyapp.data.api.ApiException
import com.wojciechkula.deepskyapp.data.api.ApiResult
import com.wojciechkula.deepskyapp.data.api.ApiSuccess
import com.wojciechkula.deepskyapp.data.datasource.LocalDataSource
import com.wojciechkula.deepskyapp.data.entity.FavouritePictureEntity
import com.wojciechkula.deepskyapp.data.mapper.FavouritePictureMapper
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

class PictureRepositoryImpl @Inject constructor(
    private val api: APODApi,
    private val localDataSource: LocalDataSource,
    private val favPicMapper: FavouritePictureMapper
) : PictureRepository {

    override suspend fun getPictureOfTheDay(): ApiResult<PictureOfTheDayModel> = handleApi { api.getPictureOfTheDay() }

    override fun getFavouritePictures() =
        localDataSource.getFavouritePictures().map { list: List<FavouritePictureEntity> ->
            list.map { favouritePictureEntity ->
                favPicMapper.mapToDomain(favouritePictureEntity)
            }
        }

    override fun getPictureByDate(date: String): LiveData<List<FavouritePictureModel>> =
        localDataSource.getPictureByDate(date)
            .map { list: List<FavouritePictureEntity> ->
                list.map { favouritePictureEntity ->
                    favPicMapper.mapToDomain(
                        favouritePictureEntity
                    )
                }
            }

    override suspend fun addFavouritePicture(picture: FavouritePictureModel): Long =
        localDataSource.saveTodayPicture(favPicMapper.mapToEntity(picture))

    override suspend fun deleteFavouritePicture(date: String): Int =
        localDataSource.deleteFavouritePicture(date)

    private suspend fun <T : Any> handleApi(
        execute: suspend () -> Response<T>
    ): ApiResult<T> {
        return try {
            val response = execute()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                ApiSuccess(body)
            } else {
                ApiError(code = response.code(), message = response.message())
            }
        } catch (e: HttpException) {
            ApiError(code = e.code(), message = e.message())
        } catch (e: Throwable) {
            ApiException(e)
        }
    }
}