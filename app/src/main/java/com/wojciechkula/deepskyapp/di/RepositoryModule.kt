package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.data.repository.PictureRepositoryImpl
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun PictureRepository(pictureRepositoryImpl: PictureRepositoryImpl): PictureRepository
}