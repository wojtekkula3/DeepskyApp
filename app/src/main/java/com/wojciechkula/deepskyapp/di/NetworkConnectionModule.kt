package com.wojciechkula.deepskyapp.di

import android.content.Context
import com.wojciechkula.utils.NetworkConnection
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class NetworkConnectionModule {

    @Provides
    @Singleton
    fun provideNetworkConnection(@ApplicationContext appContext: Context): NetworkConnection {
        return NetworkConnection(appContext)
    }
}