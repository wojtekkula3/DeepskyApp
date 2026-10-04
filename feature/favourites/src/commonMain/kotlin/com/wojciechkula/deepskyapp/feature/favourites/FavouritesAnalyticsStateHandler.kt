package com.wojciechkula.deepskyapp.feature.favourites

import com.wojciechkula.deepskyapp.core.common.Analytics
import com.wojciechkula.deepskyapp.core.mvvm.AnalyticsStateHandler
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Empty
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Loading
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesScreenState.Success

private const val SCREEN_CLASS = "Favourites"

class FavouritesAnalyticsStateHandler(private val analytics: Analytics) : AnalyticsStateHandler<FavouritesUiState>() {

    override fun mapStateToScreenName(state: FavouritesUiState): String? = when (state.screenState) {
        is Success -> "favourites"
        Empty -> "favourites_empty"
        Loading -> null
    }

    override fun sendScreenView(screenName: String) {
        analytics.logScreenView(screenName, SCREEN_CLASS)
    }
}
