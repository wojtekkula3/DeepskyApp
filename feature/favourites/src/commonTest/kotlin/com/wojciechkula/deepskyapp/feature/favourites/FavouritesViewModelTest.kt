package com.wojciechkula.deepskyapp.feature.favourites

import com.wojciechkula.deepskyapp.domain.interactor.GetFavouritePicturesInteractor
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Empty
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Success
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiAction.OpenAbout
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiAction.OpenDetails
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiEvent.AboutPressed
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesUiEvent.ItemPressed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

@OptIn(ExperimentalCoroutinesApi::class)
class FavouritesViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private fun picture(date: String) = FavouritePictureModel(
        id = 1L, copyright = null, date = date, explanation = "e",
        hdUrl = "hd", mediaType = "image", serviceVersion = "v1", title = "t-$date", url = "u",
    )

    @Test
    fun showsSuccessSortedByDateDescending() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository().apply {
            stored.value = listOf(picture("2026-07-08"), picture("2026-07-10"), picture("2026-07-09"))
        }
        val vm = FavouritesViewModel(GetFavouritePicturesInteractor(repo))
        advanceUntilIdle()
        val screenState = vm.states.value.screenState
        assertIs<Success>(screenState)
        assertEquals(listOf("2026-07-10", "2026-07-09", "2026-07-08"), screenState.pictures.map { it.date })
    }

    @Test
    fun showsEmptyWhenNoFavourites() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository()
        val vm = FavouritesViewModel(GetFavouritePicturesInteractor(repo))
        advanceUntilIdle()
        assertEquals(Empty, vm.states.value.screenState)
    }

    @Test
    fun pictureClickedEmitsOpenDetails() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository().apply { stored.value = listOf(picture("2026-07-10")) }
        val vm = FavouritesViewModel(GetFavouritePicturesInteractor(repo))
        advanceUntilIdle()

        val action = async { vm.actions.first() }
        vm.handleUiEvent(ItemPressed("2026-07-10"))
        advanceUntilIdle()

        assertEquals(OpenDetails("2026-07-10"), action.await())
    }

    @Test
    fun aboutClickedEmitsOpenAbout() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository()
        val vm = FavouritesViewModel(GetFavouritePicturesInteractor(repo))
        advanceUntilIdle()

        val action = async { vm.actions.first() }
        vm.handleUiEvent(AboutPressed)
        advanceUntilIdle()

        assertEquals(OpenAbout, action.await())
    }
}
