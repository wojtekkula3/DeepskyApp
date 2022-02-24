package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import timber.log.Timber
import javax.inject.Inject

class CheckIfPictureIsFavouriteInteractor @Inject constructor(private val repository: PictureRepository) {

    suspend operator fun invoke(date: String): Boolean =
        try {
            repository.getPictureByDate(date).isNotEmpty()
        } catch (e: Exception) {
            Timber.e(e.message)
            false
        }
}