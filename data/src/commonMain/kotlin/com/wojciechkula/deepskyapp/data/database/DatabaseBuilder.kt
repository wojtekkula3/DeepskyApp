package com.wojciechkula.deepskyapp.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

// Platform builders (`getDatabaseBuilder`, defined per source set) are wired in DI (Milestone 3);
// this common function applies the shared configuration and builds the instance.
internal fun buildAPODDatabase(builder: RoomDatabase.Builder<APODLocalDatabase>): APODLocalDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        .build()
