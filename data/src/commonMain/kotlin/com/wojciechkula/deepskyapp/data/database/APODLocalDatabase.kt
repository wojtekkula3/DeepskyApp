package com.wojciechkula.deepskyapp.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.wojciechkula.deepskyapp.data.database.dao.FavouritePictureDao
import com.wojciechkula.deepskyapp.data.database.entity.FavouritePictureEntity

// No `.db` extension on purpose: this is the file name the released 1.2.1 app used. Keeping it means
// Room opens the database those users already have and migrates it in place, instead of silently
// starting an empty one beside it and leaving every saved favourite unreachable. Do not "tidy" this.
internal const val APOD_DATABASE_NAME = "apod_database"

@Database(entities = [FavouritePictureEntity::class], version = 2)
@ConstructedBy(APODDatabaseConstructor::class)
internal abstract class APODLocalDatabase : RoomDatabase() {
    abstract fun favouritePictureDao(): FavouritePictureDao
}

@Suppress("NO_ACTUAL_FOR_EXPECT", "EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
internal expect object APODDatabaseConstructor : RoomDatabaseConstructor<APODLocalDatabase> {
    override fun initialize(): APODLocalDatabase
}
