package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.domain.FakeFavouriteRepository
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant
import kotlinx.datetime.TimeZone

class CheckIfPictureIsFavouriteInteractorTest {

    private class FixedClock(private val instant: Instant) : kotlin.time.Clock {
        override fun now(): Instant = instant
    }

    private val fixedFormatter = DateFormatter(
        clock = FixedClock(Instant.parse("2026-07-07T00:00:00Z")),
        timeZone = TimeZone.UTC,
    )

    @Test
    fun invoke_trueWhenTodayIsFavourite() = runTest {
        val repo = FakeFavouriteRepository()
        repo.favourites.value = listOf(
            FavouritePictureModel(
                date = "2026-07-07", explanation = "e", hdUrl = "h",
                mediaType = "image", serviceVersion = "v1", title = "t", url = "u",
            )
        )
        val interactor = CheckIfPictureIsFavouriteInteractor(repo, fixedFormatter)

        assertEquals(true, interactor().first())
    }

    @Test
    fun invoke_falseWhenTodayNotFavourite() = runTest {
        val repo = FakeFavouriteRepository()
        val interactor = CheckIfPictureIsFavouriteInteractor(repo, fixedFormatter)

        assertEquals(false, interactor().first())
    }
}
