package com.wojciechkula.deepskyapp.data.database

import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/** The pre-KMP schema, recreated exactly: table name with a space, snake_case columns, bitmap BLOB. */
private fun legacyVersionOne(): SQLiteConnection =
    BundledSQLiteDriver().open(":memory:").apply {
        execSQL(
            "CREATE TABLE `Favourite pictures` (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "copyright TEXT, date TEXT NOT NULL, explanation TEXT NOT NULL, hdurl TEXT NOT NULL, " +
                "media_type TEXT NOT NULL, service_version TEXT NOT NULL, title TEXT NOT NULL, " +
                "url TEXT NOT NULL, bitmap BLOB NOT NULL)"
        )
    }

private fun SQLiteConnection.insertLegacy(date: String, copyright: String? = "Tom Burnett") {
    prepare(
        "INSERT INTO `Favourite pictures` " +
            "(copyright, date, explanation, hdurl, media_type, service_version, title, url, bitmap) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)"
    ).use { statement ->
        if (copyright == null) statement.bindNull(1) else statement.bindText(1, copyright)
        statement.bindText(2, date)
        statement.bindText(3, "A distant galaxy.")
        statement.bindText(4, "https://example.com/hd.jpg")
        statement.bindText(5, "image")
        statement.bindText(6, "v1")
        statement.bindText(7, "Galaxy")
        statement.bindText(8, "https://example.com/sd.jpg")
        statement.bindBlob(9, byteArrayOf(1, 2, 3))
        statement.step()
    }
}

private fun SQLiteConnection.favouriteDates(): List<String> =
    prepare("SELECT date FROM favourite_pictures ORDER BY id").use { statement ->
        buildList { while (statement.step()) add(statement.getText(0)) }
    }

private fun SQLiteConnection.tableNames(): List<String> =
    prepare("SELECT name FROM sqlite_master WHERE type = 'table'").use { statement ->
        buildList { while (statement.step()) add(statement.getText(0)) }
    }

class MigrationsTest {

    @Test
    fun `carries a favourite saved by the pre-KMP app into the current table`() {
        val connection = legacyVersionOne().apply { insertLegacy(date = "2026-08-03") }

        MIGRATION_1_2.migrate(connection)

        connection.prepare(
            "SELECT copyright, date, explanation, hdUrl, mediaType, serviceVersion, title, url " +
                "FROM favourite_pictures"
        ).use { statement ->
            assertTrue(statement.step())
            assertEquals("Tom Burnett", statement.getText(0))
            assertEquals("2026-08-03", statement.getText(1))
            assertEquals("A distant galaxy.", statement.getText(2))
            assertEquals("https://example.com/hd.jpg", statement.getText(3))
            assertEquals("image", statement.getText(4))
            assertEquals("v1", statement.getText(5))
            assertEquals("Galaxy", statement.getText(6))
            assertEquals("https://example.com/sd.jpg", statement.getText(7))
        }
        connection.close()
    }

    @Test
    fun `keeps a missing copyright null rather than turning it into an empty string`() {
        val connection = legacyVersionOne().apply {
            insertLegacy(date = "2026-08-03", copyright = null)
        }

        MIGRATION_1_2.migrate(connection)

        connection.prepare("SELECT copyright FROM favourite_pictures").use { statement ->
            assertTrue(statement.step())
            assertTrue(statement.isNull(0))
        }
        connection.close()
    }

    @Test
    fun `collapses duplicate dates the pre-KMP app allowed`() {
        val connection = legacyVersionOne().apply {
            insertLegacy(date = "2026-08-03")
            insertLegacy(date = "2026-08-03")
            insertLegacy(date = "2026-08-02")
        }

        MIGRATION_1_2.migrate(connection)

        assertEquals(listOf("2026-08-03", "2026-08-02"), connection.favouriteDates())
        connection.close()
    }

    @Test
    fun `migrates a pre-KMP database that has no saved favourites`() {
        val connection = legacyVersionOne()

        MIGRATION_1_2.migrate(connection)

        assertEquals(emptyList(), connection.favouriteDates())
        connection.close()
    }

    @Test
    fun `drops the legacy table and never carries the bitmap across`() {
        val connection = legacyVersionOne().apply { insertLegacy(date = "2026-08-03") }

        MIGRATION_1_2.migrate(connection)

        assertFalse(connection.tableNames().contains("Favourite pictures"))
        val columns = connection.prepare("SELECT * FROM favourite_pictures").use { statement ->
            statement.step()
            (0 until statement.getColumnCount()).map { statement.getColumnName(it) }
        }
        assertFalse(columns.any { it.equals("bitmap", ignoreCase = true) })
        connection.close()
    }

    @Test
    fun `makes a duplicate date impossible afterwards`() {
        val connection = legacyVersionOne().apply { insertLegacy(date = "2026-08-03") }

        MIGRATION_1_2.migrate(connection)

        // The index is what turns the DAO's REPLACE strategy into an upsert.
        val indexes = connection.prepare(
            "SELECT name FROM sqlite_master WHERE type = 'index' AND tbl_name = 'favourite_pictures'"
        ).use { statement ->
            buildList { while (statement.step()) add(statement.getText(0)) }
        }
        assertTrue(indexes.contains("index_favourite_pictures_date"))
        connection.close()
    }
}
