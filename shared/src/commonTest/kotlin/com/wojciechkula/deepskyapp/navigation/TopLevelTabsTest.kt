package com.wojciechkula.deepskyapp.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.wojciechkula.deepskyapp.core.navigation.About
import com.wojciechkula.deepskyapp.core.navigation.Favourites
import com.wojciechkula.deepskyapp.core.navigation.PictureDetails
import com.wojciechkula.deepskyapp.core.navigation.PictureOfTheDay
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class TopLevelTabsTest {

    private fun backStack(vararg keys: NavKey) = NavBackStack(*keys)

    @Test
    fun selecting_favourites_keeps_picture_of_the_day_underneath() {
        val backStack = backStack(PictureOfTheDay)

        backStack.selectTab(TopLevelTab.Favourites)

        assertEquals(listOf(PictureOfTheDay, Favourites), backStack.toList())
    }

    @Test
    fun selecting_picture_of_the_day_returns_to_a_single_root_entry() {
        val backStack = backStack(PictureOfTheDay, Favourites)

        backStack.selectTab(TopLevelTab.PictureOfTheDay)

        assertEquals(listOf(PictureOfTheDay), backStack.toList())
    }

    @Test
    fun selecting_the_current_tab_does_not_change_the_back_stack() {
        val backStack = backStack(PictureOfTheDay, Favourites)

        backStack.selectTab(TopLevelTab.Favourites)

        assertEquals(listOf(PictureOfTheDay, Favourites), backStack.toList())
    }

    @Test
    fun selecting_a_tab_drops_a_detail_destination() {
        val backStack = backStack(PictureOfTheDay, Favourites, PictureDetails("2026-07-10"))

        backStack.selectTab(TopLevelTab.PictureOfTheDay)

        assertEquals(listOf(PictureOfTheDay), backStack.toList())
    }

    @Test
    fun selected_tab_is_null_outside_the_top_level_destinations() {
        assertEquals(TopLevelTab.PictureOfTheDay, backStack(PictureOfTheDay).selectedTab)
        assertEquals(TopLevelTab.Favourites, backStack(PictureOfTheDay, Favourites).selectedTab)
        assertNull(backStack(PictureOfTheDay, Favourites, About).selectedTab)
        assertNull(backStack(PictureOfTheDay, PictureDetails("2026-07-10")).selectedTab)
    }
}
