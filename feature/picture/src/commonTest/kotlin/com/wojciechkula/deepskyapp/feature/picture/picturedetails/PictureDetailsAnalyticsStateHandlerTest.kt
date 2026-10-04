package com.wojciechkula.deepskyapp.feature.picture.picturedetails

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.feature.picture.FakeAnalytics
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PictureDetailsAnalyticsStateHandlerTest {

    private val handler = PictureDetailsAnalyticsStateHandler(FakeAnalytics())

    private val saved = FavouritePictureModel(
        id = 1L, copyright = null, date = "2026-07-10", explanation = "e",
        hdUrl = "hd", mediaType = "image", serviceVersion = "v1", title = "t", url = "u",
    )

    private fun nameFor(screenState: PictureDetailsScreenState) =
        handler.mapStateToScreenName(PictureDetailsUiState(screenState = screenState))

    @Test
    fun everyScreenStateMapsToItsScreenName() {
        assertEquals("picture_details", nameFor(PictureDetailsScreenState.Success(saved)))
        assertEquals("picture_details_not_found", nameFor(PictureDetailsScreenState.NotFound))
    }

    @Test
    fun loadingLogsNothing() {
        assertNull(nameFor(PictureDetailsScreenState.Loading))
    }
}
