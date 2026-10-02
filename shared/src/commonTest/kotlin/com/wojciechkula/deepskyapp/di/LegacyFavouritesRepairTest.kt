package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.core.common.Logger
import com.wojciechkula.deepskyapp.core.common.NetworkMonitor
import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.interactor.RepairLegacyFavouritesInteractor
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private class RecordingPictureRepository : PictureRepository {
    val requestedDates = mutableListOf<String>()
    override suspend fun getPictureOfTheDay(): Result<PictureOfTheDayModel> = Result.NetworkError
    override suspend fun getPicture(date: String): Result<PictureOfTheDayModel> {
        requestedDates += date
        return Result.HttpError(404, "Not Found")
    }
}

private class LegacyFavouriteRepository(private val failing: Boolean = false) : FavouriteRepository {
    override fun getFavouritePictures(): Flow<List<FavouritePictureModel>> = flowOf(emptyList())
    override fun isFavourite(date: String): Flow<Boolean> = flowOf(false)
    override suspend fun addFavouritePicture(picture: FavouritePictureModel): Long = 0L
    override suspend fun deleteFavouritePicture(date: String): Int = 0
    override suspend fun getLegacyFavouritePictures(): List<FavouritePictureModel> {
        check(!failing) { "database is locked" }
        return listOf(
            FavouritePictureModel(
                id = 1L, date = "2026-08-24", explanation = "", hdUrl = "", mediaType = "image",
                serviceVersion = "v1", title = "", url = ""
            )
        )
    }
    override suspend fun updateFavouritePicture(picture: FavouritePictureModel): Int = 0
}

private class FakeNetworkMonitor(initial: Boolean) : NetworkMonitor {
    val connected = MutableStateFlow(initial)
    override val isConnected: Flow<Boolean> = connected
}

private class RecordingLogger : Logger {
    val errors = mutableListOf<String>()
    override fun d(tag: String, message: String) = Unit
    override fun e(tag: String, message: String) {
        errors += message
    }
}

class LegacyFavouritesRepairTest {

    @Test
    fun waitsForConnectivityBeforeRepairing() = runTest {
        val pictures = RecordingPictureRepository()
        val network = FakeNetworkMonitor(initial = false)
        repairLegacyFavouritesOnceOnline(network, RepairLegacyFavouritesInteractor(pictures, LegacyFavouriteRepository()), RecordingLogger())
        advanceUntilIdle()
        assertTrue(pictures.requestedDates.isEmpty())

        network.connected.value = true
        advanceUntilIdle()

        assertEquals(listOf("2026-08-24"), pictures.requestedDates)
    }

    @Test
    fun logsAFailureByTypeInsteadOfCrashing() = runTest {
        val logger = RecordingLogger()
        val job = repairLegacyFavouritesOnceOnline(
            FakeNetworkMonitor(initial = true),
            RepairLegacyFavouritesInteractor(RecordingPictureRepository(), LegacyFavouriteRepository(failing = true)),
            logger
        )
        advanceUntilIdle()

        assertTrue(job.isCompleted)
        assertEquals(listOf("Legacy favourites repair failed: IllegalStateException"), logger.errors)
    }
}
