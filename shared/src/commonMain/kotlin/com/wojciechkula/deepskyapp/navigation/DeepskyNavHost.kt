package com.wojciechkula.deepskyapp.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.wojciechkula.deepskyapp.core.common.Analytics
import com.wojciechkula.deepskyapp.core.designsystem.layout.LocalBottomOverlaySpace
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.core.navigation.About as AboutKey
import com.wojciechkula.deepskyapp.core.navigation.Favourites as FavouritesKey
import com.wojciechkula.deepskyapp.core.navigation.NavKeySavedStateConfiguration
import com.wojciechkula.deepskyapp.core.navigation.PictureDetails as PictureDetailsKey
import com.wojciechkula.deepskyapp.core.navigation.PictureOfTheDay as PictureOfTheDayKey
import com.wojciechkula.deepskyapp.feature.about.About
import com.wojciechkula.deepskyapp.feature.favourites.Favourites
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetails
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDay
import org.koin.compose.koinInject

@Suppress("ktlint:standard:property-naming")
private const val TransitionDurationMillis = 500

/**
 * The single place that maps every destination key to a screen. Features stay navigation-agnostic:
 * they take navigation as lambdas, so only this module knows the keys.
 */
@Composable
internal fun DeepskyNavHost(analytics: Analytics = koinInject()) {
    val backStack = rememberNavBackStack(NavKeySavedStateConfiguration, PictureOfTheDayKey)
    val currentKey = backStack.lastOrNull()

    LaunchedEffect(currentKey) {
        currentKey?.screenInfo()?.let { analytics.logScreenView(it.screenName, it.screenClass) }
    }

    // Not Scaffold's bottomBar: that slot lays the screen out above the card, so nothing draws behind it.
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(ApodTheme.colors.background)
    ) {
        NavDisplay(
            backStack = backStack,
            // NavDisplay's default decorator list holds only the saveable-state one, so without this
            // every entry shares the Activity's ViewModelStore: a ViewModel resolved without an
            // entry-specific key is created once and reused, so PictureDetails kept showing whichever
            // favourite was opened first and its database subscription outlived the screen. Passing a
            // list replaces the default, so the saveable-state decorator has to be repeated here.
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            // Only the three-button bar stays unconsumed, so each screen's Scaffold stops its viewport there
            // instead of letting content show through the bar's scrim; the rest is bottomContentPadding's.
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(
                    WindowInsets.safeDrawing
                        .exclude(WindowInsets.tappableElement)
                        .only(WindowInsetsSides.Bottom)
                ),
            onBack = { backStack.removeLastOrNull() },
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
                entry<PictureOfTheDayKey> {
                    UnderFloatingNavigation { PictureOfTheDay() }
                }
                entry<FavouritesKey> {
                    UnderFloatingNavigation {
                        Favourites(
                            onOpenDetails = { date -> backStack.add(PictureDetailsKey(date)) },
                            onOpenAbout = { backStack.add(AboutKey) },
                            // Not isRunning: that only turns true on the first animation frame, after this composition.
                            isEntering = LocalNavAnimatedContentScope.current.transition.run { currentState != targetState }
                        )
                    }
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

        AnimatedVisibility(
            visible = backStack.selectedTab != null,
            modifier = Modifier.align(Alignment.BottomCenter),
            enter = slideInVertically(tween(TransitionDurationMillis)) { height -> height },
            exit = slideOutVertically(tween(TransitionDurationMillis)) { height -> height }
        ) {
            FloatingBottomNavigation(
                selectedTab = backStack.activeTab,
                onTabSelected = backStack::selectTab
            )
        }
    }
}

@Composable
private fun UnderFloatingNavigation(content: @Composable () -> Unit) {
    CompositionLocalProvider(LocalBottomOverlaySpace provides ApodTheme.floatingNavigationSpace, content = content)
}
