package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.repository.APODRepository
import javax.inject.Inject

class GetPictureOfTheDayInteractor @Inject constructor(private val repository: APODRepository) {

    suspend operator fun invoke() = repository.getPictureOfTheDay()
}