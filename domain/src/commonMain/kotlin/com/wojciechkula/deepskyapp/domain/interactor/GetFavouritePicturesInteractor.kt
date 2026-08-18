package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.model.FavouritePictureModel
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow

class GetFavouritePicturesInteractor(private val repository: FavouriteRepository) {
    operator fun invoke(): Flow<List<FavouritePictureModel>> = repository.getFavouritePictures()
}
