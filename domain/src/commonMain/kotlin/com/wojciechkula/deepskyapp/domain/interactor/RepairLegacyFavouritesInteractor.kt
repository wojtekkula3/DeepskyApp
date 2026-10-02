package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.model.MediaKind
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.model.mediaKind
import com.wojciechkula.deepskyapp.domain.model.toFavourite
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository

// Favourites saved from the retired APOD API point at media URLs that no longer resolve, and those saved
// after it broke carry its placeholder title; the date is the one field that is always right.
class RepairLegacyFavouritesInteractor(
    private val pictureRepository: PictureRepository,
    private val favouriteRepository: FavouriteRepository
) {
    suspend operator fun invoke() {
        for (legacyMedia in favouriteRepository.getLegacyFavouritePictures()) {
            when (val newMedia = pictureRepository.getPicture(legacyMedia.date)) {
                is Result.Success -> if (newMedia.data.canReplaceLegacy()) {
                    favouriteRepository.updateFavouritePicture(newMedia.data.toFavourite(id = legacyMedia.id))
                }

                // Every remaining request would wait out the same timeout; the next run retries them.
                Result.NetworkError, Result.ServerNotResponding -> return

                is Result.HttpError, is Result.Exception -> Unit
            }
        }
    }
}

// The new archive has days with no media type or text; overwriting with those would lose what the user saved.
private fun PictureOfTheDayModel.canReplaceLegacy(): Boolean =
    mediaKind(mediaType, url) != MediaKind.UNSUPPORTED && explanation.isNotBlank()
