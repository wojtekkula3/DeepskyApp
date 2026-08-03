package com.wojciechkula.deepskyapp.navigation

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.wojciechkula.deepskyapp.core.navigation.Favourites as FavouritesKey
import com.wojciechkula.deepskyapp.core.navigation.PictureOfTheDay as PictureOfTheDayKey
import com.wojciechkula.deepskyapp.shared.resources.Res
import com.wojciechkula.deepskyapp.shared.resources.tab_favourites
import com.wojciechkula.deepskyapp.shared.resources.tab_picture_of_the_day
import org.jetbrains.compose.resources.StringResource

/** The destinations reachable from the bottom bar, in bar order. */
internal enum class TopLevelTab(val key: NavKey, val label: StringResource) {
    PictureOfTheDay(key = PictureOfTheDayKey, label = Res.string.tab_picture_of_the_day),
    Favourites(key = FavouritesKey, label = Res.string.tab_favourites)
}

/** The tab the bottom bar highlights, or `null` when the top entry is not a top-level destination. */
internal val NavBackStack<NavKey>.selectedTab: TopLevelTab?
    get() = TopLevelTab.entries.firstOrNull { it.key == lastOrNull() }

/**
 * Rebuilds the back stack so [tab] sits on top with Picture of The Day underneath it. Back from the
 * second tab therefore returns to the start destination — as the original app's `launchSingleTop`
 * navigation did — and repeatedly tapping tabs cannot grow the stack.
 */
internal fun NavBackStack<NavKey>.selectTab(tab: TopLevelTab) {
    if (lastOrNull() == tab.key) return
    clear()
    add(PictureOfTheDayKey)
    if (tab.key != PictureOfTheDayKey) add(tab.key)
}
