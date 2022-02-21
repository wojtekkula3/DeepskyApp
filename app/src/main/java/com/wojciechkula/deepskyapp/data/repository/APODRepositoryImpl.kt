package com.wojciechkula.deepskyapp.data.repository

import com.wojciechkula.deepskyapp.data.api.APODApi
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
import com.wojciechkula.deepskyapp.domain.repository.APODRepository
import retrofit2.Response
import javax.inject.Inject

class APODRepositoryImpl @Inject constructor(
    private val api: APODApi
) : APODRepository {

    override suspend fun getPictureOfTheDay(): Response<PictureOfTheDay> = api.getPictureOfTheDay()
}