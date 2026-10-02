package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.FakeFavouriteRepository
import com.wojciechkula.deepskyapp.domain.FakePictureRepository
import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.samplePotd
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class RepairLegacyFavouritesInteractorTest {

    private fun legacy(id: Long, date: String) = FavouritePictureModel(
        id = id,
        date = date,
        explanation = "",
        hdUrl = "https://www.nasa.gov/logo.png",
        mediaType = "image",
        serviceVersion = "v1",
        title = "NASA Science",
        url = "https://apod.nasa.gov/apod/image/2609/old.jpg"
    )

    private fun repaired(date: String) = samplePotd(date).copy(
        serviceVersion = "apod-basic",
        title = "Real title $date",
        url = "https://assets.science.nasa.gov/$date.jpg"
    )

    @Test
    fun invoke_replacesEachLegacyFavouriteWithItsRefetchedDay_keepingTheId() = runTest {
        val pictures = FakePictureRepository().apply {
            resultsByDate = mapOf(
                "2026-08-24" to Result.Success(repaired("2026-08-24")),
                "2014-09-18" to Result.Success(repaired("2014-09-18"))
            )
        }
        val favourites = FakeFavouriteRepository().apply { legacy = listOf(legacy(3, "2026-08-24"), legacy(7, "2014-09-18")) }

        RepairLegacyFavouritesInteractor(pictures, favourites)()

        assertEquals(listOf(3L, 7L), favourites.updated.map { it.id })
        assertEquals(listOf("Real title 2026-08-24", "Real title 2014-09-18"), favourites.updated.map { it.title })
        assertTrue(favourites.updated.all { it.serviceVersion == "apod-basic" })
    }

    @Test
    fun invoke_leavesADayTheApiDoesNotKnowAsItIs_andCarriesOn() = runTest {
        val pictures = FakePictureRepository().apply {
            resultsByDate = mapOf(
                "2026-08-24" to Result.HttpError(404, "Not Found"),
                "2014-09-18" to Result.Success(repaired("2014-09-18"))
            )
        }
        val favourites = FakeFavouriteRepository().apply { legacy = listOf(legacy(3, "2026-08-24"), legacy(7, "2014-09-18")) }

        RepairLegacyFavouritesInteractor(pictures, favourites)()

        assertEquals(listOf(7L), favourites.updated.map { it.id })
    }

    @Test
    fun invoke_stopsAtTheFirstConnectivityFailure() = runTest {
        val pictures = FakePictureRepository(result = Result.NetworkError)
        val favourites = FakeFavouriteRepository().apply { legacy = listOf(legacy(3, "2026-08-24"), legacy(7, "2014-09-18")) }

        RepairLegacyFavouritesInteractor(pictures, favourites)()

        assertEquals(listOf("2026-08-24"), pictures.requestedDates)
        assertTrue(favourites.updated.isEmpty())
    }

    @Test
    fun invoke_withNoLegacyFavourites_makesNoRequests() = runTest {
        val pictures = FakePictureRepository()

        RepairLegacyFavouritesInteractor(pictures, FakeFavouriteRepository())()

        assertTrue(pictures.requestedDates.isEmpty())
    }

    @Test
    fun invoke_keepsALegacyFavouriteWhenTheNewDayIsUnsupportedOrHasNoText() = runTest {
        val pictures = FakePictureRepository().apply {
            resultsByDate = mapOf(
                "2024-12-07" to Result.Success(repaired("2024-12-07").copy(mediaType = "unsupported")),
                "2014-09-18" to Result.Success(repaired("2014-09-18").copy(explanation = ""))
            )
        }
        val favourites = FakeFavouriteRepository().apply { legacy = listOf(legacy(3, "2024-12-07"), legacy(7, "2014-09-18")) }

        RepairLegacyFavouritesInteractor(pictures, favourites)()

        assertTrue(favourites.updated.isEmpty())
    }
}
