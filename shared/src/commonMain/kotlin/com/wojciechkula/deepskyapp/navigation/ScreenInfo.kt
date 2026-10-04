package com.wojciechkula.deepskyapp.navigation

import androidx.navigation3.runtime.NavKey
import com.wojciechkula.deepskyapp.core.navigation.About

internal data class ScreenInfo(
    val screenName: String,
    val screenClass: String
)

// Only destinations with no state-dependent name; the rest log through their ViewModel's AnalyticsStateHandler.
internal fun NavKey.screenInfo(): ScreenInfo? = when (this) {
    About -> ScreenInfo(screenName = "about", screenClass = "About")
    else -> null
}
