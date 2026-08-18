package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository

class AddFavouritePictureInteractor(private val repository: FavouriteRepository) {
    suspend operator fun invoke(picture: PictureOfTheDayModel): Long = repository.addFavouritePicture(
        FavouritePictureModel(
            copyright = picture.copyright,
            date = picture.date,
            explanation = picture.explanation,
            hdUrl = picture.hdUrl,
            mediaType = picture.mediaType,
            serviceVersion = picture.serviceVersion,
            title = picture.title,
            url = picture.url
        )
    )
}
