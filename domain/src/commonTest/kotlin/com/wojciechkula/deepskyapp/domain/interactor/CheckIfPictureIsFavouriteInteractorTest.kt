package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.FakeFavouriteRepository
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class CheckIfPictureIsFavouriteInteractorTest {

    @Test
    fun invoke_trueWhenDateIsFavourite() = runTest {
        val repo = FakeFavouriteRepository()
        repo.favourites.value = listOf(
            FavouritePictureModel(
                date = "2026-07-07", explanation = "e", hdUrl = "h",
                mediaType = "image", serviceVersion = "v1", title = "t", url = "u",
            )
        )
        val interactor = CheckIfPictureIsFavouriteInteractor(repo)

        assertEquals(true, interactor("2026-07-07").first())
    }

    @Test
    fun invoke_falseWhenDateNotFavourite() = runTest {
        val repo = FakeFavouriteRepository()
        repo.favourites.value = listOf(
            FavouritePictureModel(
                date = "2026-07-07", explanation = "e", hdUrl = "h",
                mediaType = "image", serviceVersion = "v1", title = "t", url = "u",
            )
        )
        val interactor = CheckIfPictureIsFavouriteInteractor(repo)

        assertEquals(false, interactor("2026-07-08").first())
    }
}
