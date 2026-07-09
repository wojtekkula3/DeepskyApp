package com.wojciechkula.deepskyapp.data.repository

import com.wojciechkula.deepskyapp.data.database.dao.FavouritePictureDao
import com.wojciechkula.deepskyapp.data.database.entity.FavouritePictureEntity
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private class FakeFavouritePictureDao : FavouritePictureDao {
    val rows = MutableStateFlow<List<FavouritePictureEntity>>(emptyList())
    private var nextId = 1L

    override fun getFavouritePictures(): Flow<List<FavouritePictureEntity>> = rows

    override fun getByDate(date: String): Flow<List<FavouritePictureEntity>> =
        rows.map { list -> list.filter { it.date == date } }

    override suspend fun add(entity: FavouritePictureEntity): Long {
        val stored = if (entity.id == 0L) entity.copy(id = nextId++) else entity
        rows.value = rows.value.filterNot { it.date == stored.date } + stored
        return stored.id
    }

    override suspend fun delete(date: String): Int {
        val before = rows.value.size
        rows.value = rows.value.filterNot { it.date == date }
        return before - rows.value.size
    }
}

private fun model(date: String = "2026-07-09") = FavouritePictureModel(
    copyright = "NASA",
    date = date,
    explanation = "A distant galaxy.",
    hdUrl = "https://example.com/hd.jpg",
    mediaType = "image",
    serviceVersion = "v1",
    title = "Galaxy",
    url = "https://example.com/sd.jpg",
)

class FavouriteRepositoryImplTest {

    @Test
    fun `adds a picture and exposes it as a mapped domain model`() = runTest {
        val dao = FakeFavouritePictureDao()
        val repository = FavouriteRepositoryImpl(dao)

        val id = repository.addFavouritePicture(model())

        assertEquals(1L, id)
        val stored = repository.getFavouritePictures().first()
        assertEquals(1, stored.size)
        val picture = stored.first()
        assertEquals("Galaxy", picture.title)
        assertEquals("https://example.com/hd.jpg", picture.hdUrl)
        assertEquals(1L, picture.id)
    }

    @Test
    fun `isFavourite reflects whether a picture with the date is stored`() = runTest {
        val dao = FakeFavouritePictureDao()
        val repository = FavouriteRepositoryImpl(dao)
        repository.addFavouritePicture(model(date = "2026-07-09"))

        assertTrue(repository.isFavourite("2026-07-09").first())
        assertFalse(repository.isFavourite("2000-01-01").first())
    }

    @Test
    fun `deletes a picture by date and returns the removed count`() = runTest {
        val dao = FakeFavouritePictureDao()
        val repository = FavouriteRepositoryImpl(dao)
        repository.addFavouritePicture(model(date = "2026-07-09"))

        val removed = repository.deleteFavouritePicture("2026-07-09")

        assertEquals(1, removed)
        assertTrue(repository.getFavouritePictures().first().isEmpty())
    }
}
