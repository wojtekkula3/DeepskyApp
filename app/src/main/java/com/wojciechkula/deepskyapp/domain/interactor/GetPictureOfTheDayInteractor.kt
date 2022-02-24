package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import javax.inject.Inject

class GetPictureOfTheDayInteractor @Inject constructor(private val repository: PictureRepository) {

    suspend operator fun invoke() = repository.getPictureOfTheDay()
}