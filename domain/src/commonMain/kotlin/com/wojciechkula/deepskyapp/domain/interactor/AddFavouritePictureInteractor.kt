package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.model.toFavourite
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository

class AddFavouritePictureInteractor(private val repository: FavouriteRepository) {
    suspend operator fun invoke(picture: PictureOfTheDayModel): Long = repository.addFavouritePicture(picture.toFavourite())
}
