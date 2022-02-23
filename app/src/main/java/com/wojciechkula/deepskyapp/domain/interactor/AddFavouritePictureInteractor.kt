package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDay
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import javax.inject.Inject

class AddFavouritePictureInteractor @Inject constructor(private val repository: PictureRepository){

    suspend operator fun invoke(picture: PictureOfTheDay) : Long = repository.addFavouritePicture(picture)
}