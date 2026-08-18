package com.wojciechkula.deepskyapp.data.database

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver

internal fun buildAPODDatabase(builder: RoomDatabase.Builder<APODLocalDatabase>): APODLocalDatabase =
    builder
        .setDriver(BundledSQLiteDriver())
        // Never fall back to a destructive migration: the app has shipped (tag 1.2.1), so dropping the
        // table would delete the user's saved favourites. Every schema change gets a migration instead.
        .addMigrations(MIGRATION_1_2)
        .build()
