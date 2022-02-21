package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.data.repository.APODRepositoryImpl
import com.wojciechkula.deepskyapp.domain.repository.APODRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun APODRepository(apodRepositoryImpl: APODRepositoryImpl): APODRepository
}