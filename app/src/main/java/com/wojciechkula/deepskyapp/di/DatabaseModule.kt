package com.wojciechkula.deepskyapp.di

import android.content.Context
import com.wojciechkula.deepskyapp.data.dao.FavouritePictureDao
import com.wojciechkula.deepskyapp.data.database.APODLocalDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {

    @Provides
    fun provideFavouritePictureDao(database: APODLocalDatabase): FavouritePictureDao {
        return database.favouritePictureDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): APODLocalDatabase {
        return APODLocalDatabase.getDatabase(appContext)
    }
}