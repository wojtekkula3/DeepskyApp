package com.wojciechkula.deepskyapp.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

// Platform builders (`getDatabaseBuilder`, defined per source set) are wired in DI (Milestone 3);
// this common function applies the shared configuration and builds the instance.
internal fun buildAPODDatabase(builder: RoomDatabase.Builder<APODLocalDatabase>): APODLocalDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        // v2 added the unique index on `date`. The app is unreleased, so dropping the table is cheaper
        // than a migration that would also have to de-duplicate rows written under v1.
        .fallbackToDestructiveMigration(dropAllTables = true)
        .build()
