package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import javax.inject.Inject

class GetFavouritePicturesInteractor @Inject constructor(private val repository: PictureRepository) {

    operator fun invoke() = repository.getFavouritePictures()
}