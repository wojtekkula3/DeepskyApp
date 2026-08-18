package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository

class DeleteFavouritePictureInteractor(private val repository: FavouriteRepository) {
    suspend operator fun invoke(date: String): Int = repository.deleteFavouritePicture(date)
}
