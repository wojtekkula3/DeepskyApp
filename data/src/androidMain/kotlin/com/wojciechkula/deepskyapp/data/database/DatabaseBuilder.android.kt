package com.wojciechkula.deepskyapp.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase

internal fun getDatabaseBuilder(context: Context): RoomDatabase.Builder<APODLocalDatabase> {
    val appContext = context.applicationContext
    val dbFile = appContext.getDatabasePath(APOD_DATABASE_NAME)
    return Room.databaseBuilder<APODLocalDatabase>(
        context = appContext,
        name = dbFile.absolutePath,
    )
}
