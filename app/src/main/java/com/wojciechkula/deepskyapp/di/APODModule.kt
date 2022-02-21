package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.data.api.APODApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object APODModule {
    private const val BASE_URL = "https://api.nasa.gov/"

    @Singleton
    @Provides
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .build()

    @Singleton
    @Provides
    fun provideAPODApi(retrofit: Retrofit): APODApi = retrofit.create(APODApi::class.java)

}