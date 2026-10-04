package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import com.wojciechkula.deepskyapp.feature.picture.FakeAnalytics
import com.wojciechkula.deepskyapp.feature.picture.sampleApod
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PictureOfTheDayAnalyticsStateHandlerTest {

    private val handler = PictureOfTheDayAnalyticsStateHandler(FakeAnalytics())

    private fun nameFor(screenState: PictureOfTheDayScreenState) =
        handler.mapStateToScreenName(PictureOfTheDayUiState(screenState = screenState))

    @Test
    fun everyScreenStateMapsToItsScreenName() {
        assertEquals("picture_of_the_day", nameFor(PictureOfTheDayScreenState.Success(sampleApod)))
        assertEquals("picture_of_the_day_error", nameFor(PictureOfTheDayScreenState.Error))
        assertEquals("picture_of_the_day_server_error", nameFor(PictureOfTheDayScreenState.ServerError))
        assertEquals("picture_of_the_day_server_unreachable", nameFor(PictureOfTheDayScreenState.ServerUnreachable))
        assertEquals("picture_of_the_day_no_internet", nameFor(PictureOfTheDayScreenState.NoInternet))
    }

    @Test
    fun loadingLogsNothing() {
        assertNull(nameFor(PictureOfTheDayScreenState.Loading))
    }
}
