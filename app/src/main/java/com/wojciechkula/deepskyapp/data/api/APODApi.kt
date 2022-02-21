package com.wojciechkula.deepskyapp.data.api

import com.wojciechkula.deepskyapp.BuildConfig
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

interface APODApi {

    @GET("planetary/apod")
    suspend fun getPictureOfTheDay(
        @Query("api_key")
        apiKey: String = BuildConfig.APOD_API_KEY,
        @Query("date")
        date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ): Response<PictureOfTheDay>

}