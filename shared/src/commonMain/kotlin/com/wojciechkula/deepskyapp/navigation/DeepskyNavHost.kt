package com.wojciechkula.deepskyapp.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.wojciechkula.deepskyapp.feature.about.About
import com.wojciechkula.deepskyapp.core.navigation.About as AboutKey
import com.wojciechkula.deepskyapp.core.navigation.Favourites as FavouritesKey
import com.wojciechkula.deepskyapp.core.navigation.NavKeySavedStateConfiguration
import com.wojciechkula.deepskyapp.core.navigation.PictureDetails as PictureDetailsKey
import com.wojciechkula.deepskyapp.core.navigation.PictureOfTheDay as PictureOfTheDayKey
import com.wojciechkula.deepskyapp.feature.favourites.Favourites
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetails
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDay

private const val TransitionDurationMillis = 500

/**
 * The single place that maps every destination key to a screen. Features stay navigation-agnostic:
 * they take navigation as lambdas, so only this module knows the keys.
 */
@Composable
internal fun DeepskyNavHost() {
    val backStack = rememberNavBackStack(NavKeySavedStateConfiguration, PictureOfTheDayKey)
    val selectedTab = backStack.selectedTab

    Scaffold(
        bottomBar = {
            // Hidden on Details and About, as in the original app.
            if (selectedTab != null) {
                DeepskyBottomBar(
                    selectedTab = selectedTab,
                    onTabSelected = backStack::selectTab
                )
            }
        }
    ) { padding ->
        NavDisplay(
            backStack = backStack,
            // Every screen owns its own Scaffold, and `padding` offsets content without consuming the
            // window insets it was computed from — so without `consumeWindowInsets` the inner Scaffolds
            // measure the system bars again and apply the status-bar and navigation-bar insets a second
            // time, leaving a doubled gap at the top of every screen.
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding),
            onBack = { backStack.removeLastOrNull() },
            // The original app's enter_right_to_left / exit_right_to_left pair (a 500 ms horizontal
            // slide), reversed on back.
            transitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(TransitionDurationMillis),
                    initialOffsetX = { width -> width }
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(TransitionDurationMillis),
                    targetOffsetX = { width -> -width }
                )
            },
            popTransitionSpec = {
                slideInHorizontally(
                    animationSpec = tween(TransitionDurationMillis),
                    initialOffsetX = { width -> -width }
                ) togetherWith slideOutHorizontally(
                    animationSpec = tween(TransitionDurationMillis),
                    targetOffsetX = { width -> width }
                )
            },
            entryProvider = entryProvider {
                entry<PictureOfTheDayKey> { PictureOfTheDay() }
                entry<FavouritesKey> {
                    Favourites(
                        onOpenDetails = { date -> backStack.add(PictureDetailsKey(date)) },
                        onOpenAbout = { backStack.add(AboutKey) }
                    )
                }
                entry<PictureDetailsKey> { key ->
                    PictureDetails(
                        date = key.date,
                        onBack = { backStack.removeLastOrNull() }
                    )
                }
                entry<AboutKey> { About(onBack = { backStack.removeLastOrNull() }) }
            }
        )
    }
}
