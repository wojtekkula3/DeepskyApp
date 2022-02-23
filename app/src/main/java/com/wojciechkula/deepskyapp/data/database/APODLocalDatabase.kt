package com.wojciechkula.deepskyapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wojciechkula.deepskyapp.data.dao.FavouritePictureDao
import com.wojciechkula.deepskyapp.data.entity.FavouritePictureEntity

@Database(
    entities = [FavouritePictureEntity::class],
    version = 1,
)
abstract class APODLocalDatabase : RoomDatabase() {

    abstract fun favouritePictureDao(): FavouritePictureDao

    companion object {
        @Volatile
        private var INSTANCE: APODLocalDatabase? = null

        fun getDatabase(context: Context): APODLocalDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }
            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    APODLocalDatabase::class.java,
                    "apod_database"
                ).build()
                INSTANCE = instance
                return instance
            }
        }
    }
}