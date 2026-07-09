package com.wojciechkula.deepskyapp.domain.interactor

import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository

class GetPictureOfTheDayInteractor(
    private val repository: PictureRepository,
) {
    suspend operator fun invoke(): Result<PictureOfTheDayModel> =
        repository.getPictureOfTheDay()
}
