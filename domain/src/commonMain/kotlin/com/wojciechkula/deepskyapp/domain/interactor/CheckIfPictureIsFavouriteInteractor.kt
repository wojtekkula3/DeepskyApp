package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import kotlinx.coroutines.flow.Flow

class CheckIfPictureIsFavouriteInteractor(
    private val repository: FavouriteRepository,
    private val dateFormatter: DateFormatter,
) {
    operator fun invoke(): Flow<Boolean> =
        repository.isFavourite(dateFormatter.currentApodDate())
}
