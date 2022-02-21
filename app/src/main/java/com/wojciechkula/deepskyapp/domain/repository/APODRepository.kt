package com.wojciechkula.deepskyapp.domain.repository

import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
import retrofit2.Response

interface APODRepository {

    suspend fun getPictureOfTheDay() : Response<PictureOfTheDay>
}