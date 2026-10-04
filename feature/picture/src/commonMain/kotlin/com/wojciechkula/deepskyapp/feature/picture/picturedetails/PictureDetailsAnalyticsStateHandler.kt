package com.wojciechkula.deepskyapp.feature.picture.picturedetails

import com.wojciechkula.deepskyapp.core.common.Analytics
import com.wojciechkula.deepskyapp.core.mvvm.AnalyticsStateHandler
import com.wojciechkula.deepskyapp.feature.picture.OpenedMedia
import com.wojciechkula.deepskyapp.feature.picture.logFavouriteRemoved
import com.wojciechkula.deepskyapp.feature.picture.logMediaOpenClicked
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Loading
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.NotFound
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsScreenState.Success

private const val SCREEN_CLASS = "PictureDetails"
private const val SOURCE = "picture_details"

class PictureDetailsAnalyticsStateHandler(private val analytics: Analytics) : AnalyticsStateHandler<PictureDetailsUiState>() {

    override fun mapStateToScreenName(state: PictureDetailsUiState): String? = when (state.screenState) {
        is Success -> "picture_details"
        NotFound -> "picture_details_not_found"
        Loading -> null
    }

    override fun sendScreenView(screenName: String) {
        analytics.logScreenView(screenName, SCREEN_CLASS)
    }

    internal fun onFavouriteRemoved() = analytics.logFavouriteRemoved(SOURCE)

    internal fun onMediaOpenPressed(media: OpenedMedia) = analytics.logMediaOpenClicked(SOURCE, media)
}
