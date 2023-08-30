package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.data.api.APODApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object APODModule {
    private const val BASE_URL = "https://api.nasa.gov/"

    @Singleton
    @Provides
    fun provideAPODApi(): APODApi {
        val client = OkHttpClient.Builder()
            .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(client)
            .build()

        return retrofit.create(APODApi::class.java)
    }
}