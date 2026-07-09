package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.FakeFavouriteRepository
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetFavouritePicturesInteractorTest {

    @Test
    fun invoke_emitsFavouritesFromRepository() = runTest {
        val repo = FakeFavouriteRepository()
        val item = FavouritePictureModel(
            date = "2026-07-07", explanation = "e", hdUrl = "h",
            mediaType = "image", serviceVersion = "v1", title = "t", url = "u",
        )
        repo.favourites.value = listOf(item)
        val interactor = GetFavouritePicturesInteractor(repo)

        assertEquals(listOf(item), interactor().first())
    }
}
