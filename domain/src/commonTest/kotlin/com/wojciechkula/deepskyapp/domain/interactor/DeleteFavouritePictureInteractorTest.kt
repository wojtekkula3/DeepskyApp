package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.FakeFavouriteRepository
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class DeleteFavouritePictureInteractorTest {

    @Test
    fun invoke_deletesByDate() = runTest {
        val repo = FakeFavouriteRepository()
        repo.favourites.value = listOf(
            FavouritePictureModel(
                date = "2026-07-07", explanation = "e", hdUrl = "h",
                mediaType = "image", serviceVersion = "v1", title = "t", url = "u",
            )
        )
        val interactor = DeleteFavouritePictureInteractor(repo)

        val deleted = interactor("2026-07-07")

        assertEquals(1, deleted)
        assertEquals(listOf("2026-07-07"), repo.deletedDates)
    }
}
