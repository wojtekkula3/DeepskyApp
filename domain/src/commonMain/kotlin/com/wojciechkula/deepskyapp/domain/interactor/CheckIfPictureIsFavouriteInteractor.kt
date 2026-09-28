package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow

class CheckIfPictureIsFavouriteInteractor(private val repository: FavouriteRepository) {
    operator fun invoke(date: String): Flow<Boolean> = repository.isFavourite(date)
}
