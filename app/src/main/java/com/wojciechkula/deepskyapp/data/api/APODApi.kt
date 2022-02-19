package com.wojciechkula.deepskyapp.data.api

import com.wojciechkula.deepskyapp.BuildConfig
import com.wojciechkula.deepskyapp.data.entity.PictureOfTheDayEntity
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.text.SimpleDateFormat
import java.util.*

interface APODApi {

    companion object {

        const val BASE_URL = "https://api.nasa.gov/"
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        val api by lazy {
            retrofit.create(APODApi::class.java)
        }
    }

    @GET("planetary/apod")
    suspend fun getPictureOfTheDay(
        @Query("api_key")
        apiKey: String = BuildConfig.APOD_API_KEY,
        @Query("date")
        date: String = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    ): Response<PictureOfTheDayEntity>

}