package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.interactor.AddFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.CheckIfPictureIsFavouriteInteractor
import com.wojciechkula.deepskyapp.domain.interactor.DeleteFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetPictureOfTheDayInteractor
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.feature.picture.FakeFavouriteRepository
import com.wojciechkula.deepskyapp.feature.picture.FakeNetworkMonitor
import com.wojciechkula.deepskyapp.feature.picture.FakePictureRepository
import com.wojciechkula.deepskyapp.feature.picture.sampleApod
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Error
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.NoInternet
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.FavouritePressed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Paused
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.Resumed
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayUiEvent.RetryPressed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class PictureOfTheDayViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    // DateFormatter fixed to sampleApod.date so CheckIfPictureIsFavourite queries "2026-07-12".
    private val fixedDateFormatter = DateFormatter(
        clock = object : Clock {
            override fun now(): Instant = Instant.parse("2026-07-12T12:00:00Z")
        },
        timeZone = kotlinx.datetime.TimeZone.UTC,
    )

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private class MutableClock(var instant: Instant) : Clock {
        override fun now(): Instant = instant
    }

    private fun buildViewModel(
        pictureResult: Result<PictureOfTheDayModel> = Result.Success(sampleApod),
        favouriteRepo: FakeFavouriteRepository = FakeFavouriteRepository(),
        networkMonitor: FakeNetworkMonitor = FakeNetworkMonitor(initial = true),
        clock: Clock = MutableClock(Instant.parse("2026-07-12T12:00:00Z")),
    ): PictureOfTheDayViewModel {
        val pictureRepo = FakePictureRepository(pictureResult)
        return PictureOfTheDayViewModel(
            getPictureOfTheDay = GetPictureOfTheDayInteractor(pictureRepo),
            checkIfPictureIsFavourite = CheckIfPictureIsFavouriteInteractor(favouriteRepo, fixedDateFormatter),
            addFavouritePicture = AddFavouritePictureInteractor(favouriteRepo),
            deleteFavouritePicture = DeleteFavouritePictureInteractor(favouriteRepo),
            networkMonitor = networkMonitor,
            clock = clock,
        )
    }

    @Test
    fun showsSuccessWhenConnected() = runTest(dispatcher) {
        val vm = buildViewModel()
        advanceUntilIdle()
        val screenState = vm.states.value.screenState
        assertIs<Success>(screenState)
        assertEquals(sampleApod, screenState.picture)
    }

    @Test
    fun showsNoInternetWhenOffline() = runTest(dispatcher) {
        val vm = buildViewModel(networkMonitor = FakeNetworkMonitor(initial = false))
        advanceUntilIdle()
        assertEquals(NoInternet, vm.states.value.screenState)
    }

    @Test
    fun showsErrorScreenOnHttpError() = runTest(dispatcher) {
        val vm = buildViewModel(pictureResult = Result.HttpError(500, "boom"))
        advanceUntilIdle()
        assertEquals(Error, vm.states.value.screenState)
    }

    @Test
    fun showsErrorScreenOnException() = runTest(dispatcher) {
        val vm = buildViewModel(pictureResult = Result.Exception(RuntimeException("net down")))
        advanceUntilIdle()
        assertEquals(Error, vm.states.value.screenState)
    }

    @Test
    fun favouriteToggleAddsThenRemoves() = runTest(dispatcher) {
        val favouriteRepo = FakeFavouriteRepository()
        val vm = buildViewModel(favouriteRepo = favouriteRepo)
        advanceUntilIdle()

        vm.handleUiEvent(FavouritePressed)      // not favourite -> add
        advanceUntilIdle()
        assertEquals(1, favouriteRepo.stored.value.size)
        assertTrue(vm.states.value.isFavourite)

        vm.handleUiEvent(FavouritePressed)      // now favourite -> delete
        advanceUntilIdle()
        assertTrue(favouriteRepo.stored.value.isEmpty())
        assertTrue(!vm.states.value.isFavourite)
    }

    @Test
    fun countdownTicksAfterResumeAndStopsOnPause() = runTest(dispatcher) {
        val clock = MutableClock(Instant.parse("2026-07-12T12:00:00Z"))
        val vm = buildViewModel(clock = clock)
        advanceUntilIdle()                       // load -> Success (no ticker yet: not resumed)
        assertEquals("", vm.states.value.timeToNewPicture)

        // The ticker never completes on its own, so Paused must run even if an assertion fails —
        // otherwise runTest's trailing advanceUntilIdle() would spin on it forever and the test would
        // hang instead of reporting the failure.
        try {
            vm.handleUiEvent(Resumed)
            runCurrent()                         // first emission happens before the first delay
            val firstTick = vm.states.value.timeToNewPicture
            assertTrue(firstTick.isNotEmpty(), "countdown should emit immediately after Resumed")

            clock.instant += 1.seconds        // the ticker formats from the clock, not from virtual time
            advanceTimeBy(1.seconds)
            runCurrent()
            assertNotEquals(firstTick, vm.states.value.timeToNewPicture, "countdown should keep ticking")
        } finally {
            vm.handleUiEvent(Paused)
        }

        val afterPause = vm.states.value.timeToNewPicture
        clock.instant += 5.seconds
        advanceTimeBy(5.seconds)
        runCurrent()
        assertEquals(afterPause, vm.states.value.timeToNewPicture, "countdown should stop after Paused")
    }

    @Test
    fun retryReloadsAfterAnError() = runTest(dispatcher) {
        val pictureRepo = FakePictureRepository(Result.HttpError(500, "boom"))
        val vm = PictureOfTheDayViewModel(
            getPictureOfTheDay = GetPictureOfTheDayInteractor(pictureRepo),
            checkIfPictureIsFavourite = CheckIfPictureIsFavouriteInteractor(FakeFavouriteRepository(), fixedDateFormatter),
            addFavouritePicture = AddFavouritePictureInteractor(FakeFavouriteRepository()),
            deleteFavouritePicture = DeleteFavouritePictureInteractor(FakeFavouriteRepository()),
            networkMonitor = FakeNetworkMonitor(initial = true),
            clock = MutableClock(Instant.parse("2026-07-12T12:00:00Z")),
        )
        advanceUntilIdle()
        assertEquals(Error, vm.states.value.screenState)

        pictureRepo.result = Result.Success(sampleApod)
        vm.handleUiEvent(RetryPressed)
        advanceUntilIdle()

        assertIs<Success>(vm.states.value.screenState)
    }

    @Test
    fun reconnectAfterOfflineLoadsThePicture() = runTest(dispatcher) {
        val networkMonitor = FakeNetworkMonitor(initial = false)
        val vm = buildViewModel(networkMonitor = networkMonitor)
        advanceUntilIdle()
        assertEquals(NoInternet, vm.states.value.screenState)

        networkMonitor.connected.value = true
        advanceUntilIdle()

        assertIs<Success>(vm.states.value.screenState)
    }

    @Test
    fun connectivityDropAfterSuccessKeepsThePicture() = runTest(dispatcher) {
        val networkMonitor = FakeNetworkMonitor(initial = true)
        val vm = buildViewModel(networkMonitor = networkMonitor)
        advanceUntilIdle()
        assertIs<Success>(vm.states.value.screenState)

        networkMonitor.connected.value = false
        advanceUntilIdle()

        assertIs<Success>(vm.states.value.screenState)
    }

    @Test
    fun favouritePressedIsIgnoredWhileNotLoaded() = runTest(dispatcher) {
        val favouriteRepo = FakeFavouriteRepository()
        val vm = buildViewModel(
            favouriteRepo = favouriteRepo,
            networkMonitor = FakeNetworkMonitor(initial = false),
        )
        advanceUntilIdle()

        vm.handleUiEvent(FavouritePressed)
        advanceUntilIdle()

        assertTrue(favouriteRepo.stored.value.isEmpty())
    }
}
