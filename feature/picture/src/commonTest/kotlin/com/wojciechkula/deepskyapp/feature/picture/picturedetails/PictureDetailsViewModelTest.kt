package com.wojciechkula.deepskyapp.feature.picture.picturedetails

import com.wojciechkula.deepskyapp.domain.interactor.DeleteFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetFavouritePicturesInteractor
import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.feature.picture.FakeFavouriteRepository
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Success
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiAction.NavigateBack
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.BackPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.DeleteConfirmedPressed
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsUiEvent.SnackbarDismissed
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
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class PictureDetailsViewModelTest {

    private val dispatcher = StandardTestDispatcher()

    @BeforeTest fun setUp() { Dispatchers.setMain(dispatcher) }
    @AfterTest fun tearDown() { Dispatchers.resetMain() }

    private val saved = FavouritePictureModel(
        id = 1L, copyright = "NASA", date = "2026-07-10", explanation = "e",
        hdUrl = "hd", mediaType = "image", serviceVersion = "v1", title = "t", url = "u",
    )

    private fun buildViewModel(repo: FakeFavouriteRepository): PictureDetailsViewModel =
        PictureDetailsViewModel(
            date = saved.date,
            getFavouritePictures = GetFavouritePicturesInteractor(repo),
            deleteFavouritePicture = DeleteFavouritePictureInteractor(repo),
        )

    @Test
    fun loadsPictureMatchingDate() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository().apply { stored.value = listOf(saved) }
        val vm = buildViewModel(repo)
        advanceUntilIdle()
        val screenState = vm.states.value.screenState
        assertIs<Success>(screenState)
        assertEquals(saved, screenState.picture)
    }

    @Test
    fun deleteConfirmedRemovesAndEmitsNavigateBack() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository().apply { stored.value = listOf(saved) }
        val vm = buildViewModel(repo)
        advanceUntilIdle()

        val action = async { vm.actions.first() }
        vm.handleUiEvent(DeleteConfirmedPressed)
        advanceUntilIdle()

        assertEquals(NavigateBack, action.await())
        assertTrue(repo.stored.value.isEmpty())
    }

    @Test
    fun deleteFailureShowsASnackbarThatCanBeDismissed() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository().apply {
            stored.value = listOf(saved)
            throwOnDelete = true
        }
        val vm = buildViewModel(repo)
        advanceUntilIdle()

        vm.handleUiEvent(DeleteConfirmedPressed)
        advanceUntilIdle()
        // The message is a state enum rather than the exception's text: the screen resolves the copy,
        // so no Throwable.message can reach the UI.
        assertEquals(PictureDetailsMessage.DeleteFailed, vm.states.value.snackbarMessage)

        vm.handleUiEvent(SnackbarDismissed)
        assertNull(vm.states.value.snackbarMessage)
    }

    @Test
    fun showsNotFoundWhenTheDateIsNotAFavourite() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository()      // empty store
        val vm = buildViewModel(repo)
        advanceUntilIdle()

        assertEquals(PictureDetailsScreenState.NotFound, vm.states.value.screenState)
    }

    @Test
    fun keepsTheLoadedPictureWhenItIsRemovedFromFavourites() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository().apply { stored.value = listOf(saved) }
        val vm = buildViewModel(repo)
        advanceUntilIdle()
        assertIs<Success>(vm.states.value.screenState)

        repo.stored.value = emptyList()           // deleted elsewhere: do not blank the screen
        advanceUntilIdle()

        assertIs<Success>(vm.states.value.screenState)
    }

    @Test
    fun backClickedEmitsNavigateBack() = runTest(dispatcher) {
        val repo = FakeFavouriteRepository().apply { stored.value = listOf(saved) }
        val vm = buildViewModel(repo)
        advanceUntilIdle()

        val action = async { vm.actions.first() }
        vm.handleUiEvent(BackPressed)
        advanceUntilIdle()

        assertEquals(NavigateBack, action.await())
    }
}
