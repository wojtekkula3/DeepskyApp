package com.wojciechkula.deepskyapp.domain.repository

import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel

interface PictureRepository {
    suspend fun getPictureOfTheDay(): Result<PictureOfTheDayModel>
}
