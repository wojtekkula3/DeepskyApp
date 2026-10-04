package com.wojciechkula.deepskyapp.feature.favourites

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class FavouritesAnalyticsStateHandlerTest {

    private val handler = FavouritesAnalyticsStateHandler(FakeAnalytics())

    private fun nameFor(screenState: FavouritesScreenState) =
        handler.mapStateToScreenName(FavouritesUiState(screenState = screenState))

    @Test
    fun everyScreenStateMapsToItsScreenName() {
        assertEquals("favourites", nameFor(FavouritesScreenState.Success(emptyList())))
        assertEquals("favourites_empty", nameFor(FavouritesScreenState.Empty))
    }

    @Test
    fun loadingLogsNothing() {
        assertNull(nameFor(FavouritesScreenState.Loading))
    }
}
