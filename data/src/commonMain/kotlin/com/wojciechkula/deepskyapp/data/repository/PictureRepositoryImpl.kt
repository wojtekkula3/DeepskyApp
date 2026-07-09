package com.wojciechkula.deepskyapp.data.repository

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.data.api.APODApi
import com.wojciechkula.deepskyapp.data.api.dto.PictureOfTheDayDto
import com.wojciechkula.deepskyapp.data.mapper.toDomain
import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import io.ktor.client.call.body
import io.ktor.http.isSuccess

internal class PictureRepositoryImpl(
    private val api: APODApi,
    private val dateFormatter: DateFormatter,
) : PictureRepository {

    override suspend fun getPictureOfTheDay(): Result<PictureOfTheDayModel> =
        try {
            val response = api.getPictureOfTheDay(dateFormatter.currentApodDate())
            if (response.status.isSuccess()) {
                Result.Success(response.body<PictureOfTheDayDto>().toDomain())
            } else {
                Result.HttpError(response.status.value, response.status.description)
            }
        } catch (throwable: Throwable) {
            Result.Exception(throwable)
        }
}
