package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import com.wojciechkula.deepskyapp.core.common.Analytics
import com.wojciechkula.deepskyapp.core.mvvm.AnalyticsStateHandler
import com.wojciechkula.deepskyapp.feature.picture.OpenedMedia
import com.wojciechkula.deepskyapp.feature.picture.logFavouriteRemoved
import com.wojciechkula.deepskyapp.feature.picture.logMediaOpenClicked
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Error
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Loading
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.NoInternet
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.ServerError
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.ServerUnreachable
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayScreenState.Success

private const val SCREEN_CLASS = "PictureOfTheDay"
private const val SOURCE = "picture_of_the_day"

class PictureOfTheDayAnalyticsStateHandler(private val analytics: Analytics) : AnalyticsStateHandler<PictureOfTheDayUiState>() {

    override fun mapStateToScreenName(state: PictureOfTheDayUiState): String? = when (state.screenState) {
        is Success -> "picture_of_the_day"
        Error -> "picture_of_the_day_error"
        ServerError -> "picture_of_the_day_server_error"
        ServerUnreachable -> "picture_of_the_day_server_unreachable"
        NoInternet -> "picture_of_the_day_no_internet"
        Loading -> null
    }

    override fun sendScreenView(screenName: String) {
        analytics.logScreenView(screenName, SCREEN_CLASS)
    }

    internal fun onFavouriteRemoved() = analytics.logFavouriteRemoved(SOURCE)

    internal fun onMediaOpenPressed(media: OpenedMedia) = analytics.logMediaOpenClicked(SOURCE, media)
}
