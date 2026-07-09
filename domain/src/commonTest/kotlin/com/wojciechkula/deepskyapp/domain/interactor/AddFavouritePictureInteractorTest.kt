package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.FakeFavouriteRepository
import com.wojciechkula.deepskyapp.domain.samplePotd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class AddFavouritePictureInteractorTest {

    @Test
    fun invoke_mapsPotdAndAddsToRepository() = runTest {
        val repo = FakeFavouriteRepository()
        val interactor = AddFavouritePictureInteractor(repo)
        val potd = samplePotd(date = "2026-07-07")

        val id = interactor(potd)

        assertEquals(1L, id)
        assertEquals(1, repo.added.size)
        val saved = repo.added.first()
        assertEquals("2026-07-07", saved.date)
        assertEquals(potd.title, saved.title)
        assertEquals(potd.hdUrl, saved.hdUrl)
        assertEquals(null, saved.id)
    }
}
