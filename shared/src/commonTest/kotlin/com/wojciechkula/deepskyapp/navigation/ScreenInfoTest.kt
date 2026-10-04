package com.wojciechkula.deepskyapp.navigation

import com.wojciechkula.deepskyapp.core.navigation.About
import com.wojciechkula.deepskyapp.core.navigation.Favourites
import com.wojciechkula.deepskyapp.core.navigation.PictureDetails
import com.wojciechkula.deepskyapp.core.navigation.PictureOfTheDay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ScreenInfoTest {

    @Test
    fun about_maps_to_its_screen_info() {
        assertEquals(ScreenInfo(screenName = "about", screenClass = "About"), About.screenInfo())
    }

    @Test
    fun screens_logged_by_their_view_model_have_no_screen_info() {
        assertNull(PictureOfTheDay.screenInfo())
        assertNull(Favourites.screenInfo())
        assertNull(PictureDetails("2026-10-04").screenInfo())
    }
}
