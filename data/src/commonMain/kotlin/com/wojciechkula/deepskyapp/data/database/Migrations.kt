package com.wojciechkula.deepskyapp.data.database

import androidx.room.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

// The pre-KMP table name. It really did contain a space, hence the quoting everywhere.
private const val LEGACY_TABLE = "Favourite pictures"

// Copied verbatim from schemas/2.json — Room validates the result of a migration against that schema
// and fails loudly if a single column or constraint differs.
private const val CREATE_FAVOURITE_PICTURES =
    "CREATE TABLE IF NOT EXISTS `favourite_pictures` (" +
        "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `copyright` TEXT, `date` TEXT NOT NULL, " +
        "`explanation` TEXT NOT NULL, `hdUrl` TEXT NOT NULL, `mediaType` TEXT NOT NULL, " +
        "`serviceVersion` TEXT NOT NULL, `title` TEXT NOT NULL, `url` TEXT NOT NULL)"

private const val CREATE_UNIQUE_DATE_INDEX =
    "CREATE UNIQUE INDEX IF NOT EXISTS `index_favourite_pictures_date` " +
        "ON `favourite_pictures` (`date`)"

// The legacy `bitmap` BLOB is not carried over: favourites store metadata only and Coil re-fetches the
// image from `url`. `id` is left out so SQLite assigns fresh ones.
private const val COPY_FROM_LEGACY =
    "INSERT OR IGNORE INTO `favourite_pictures` " +
        "(`copyright`, `date`, `explanation`, `hdUrl`, `mediaType`, `serviceVersion`, `title`, `url`) " +
        "SELECT `copyright`, `date`, `explanation`, `hdurl`, `media_type`, `service_version`, " +
        "`title`, `url` FROM `$LEGACY_TABLE`"

/**
 * Moves the database the released 1.2.1 app created up to version 2: the favourites are copied out of
 * the old `Favourite pictures` table into the current one, which is what makes them survive the
 * upgrade, and the old table is dropped.
 *
 * The index is created *before* the copy so `INSERT OR IGNORE` collapses the duplicate dates the old
 * app could produce. The `bitmap` BLOB is not carried over.
 *
 * Version 1 always means the pre-KMP schema, so this needs no branching: a fresh install starts at
 * `user_version = 0` and goes through `onCreate` at version 2 instead, and the old app created its
 * table as soon as it opened the database, so a user with no saved favourites still has an empty table
 * rather than a missing one.
 *
 * **When this whole file can go:** once Play Console shows no active devices on `versionCode <= 10`
 * (the last pre-KMP release, 1.2.1). Delete `MIGRATION_1_2`, its constants, `MigrationsTest`, the
 * `sqlite-bundledJvm` test dependency and `schemas/1.json`, and in the same change add
 * `.fallbackToDestructiveMigrationFrom(dropAllTables = true, 1)`. It has to be the same change: Room
 * throws if a version appears both there and as a migration's start or end version. Dropping the
 * migration *without* that fallback does not lose data quietly — Room fails to open the database and
 * the app crashes on launch. `APOD_DATABASE_NAME` must keep its value either way.
 */
internal val MIGRATION_1_2 = object : Migration(startVersion = 1, endVersion = 2) {
    override fun migrate(connection: SQLiteConnection) {
        connection.execSQL(CREATE_FAVOURITE_PICTURES)
        connection.execSQL(CREATE_UNIQUE_DATE_INDEX)
        connection.execSQL(COPY_FROM_LEGACY)
        connection.execSQL("DROP TABLE `$LEGACY_TABLE`")
    }
}
