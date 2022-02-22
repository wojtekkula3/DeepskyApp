package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.initializer.AppInitializersContainer
import com.wojciechkula.deepskyapp.initializer.TimberInitializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
class AppInitializerModule {

    @Provides
    fun provideAppInitializersContainer(timberInitializer: TimberInitializer) =
        AppInitializersContainer(timberInitializer)
}