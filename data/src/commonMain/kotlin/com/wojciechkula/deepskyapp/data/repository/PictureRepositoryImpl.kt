package com.wojciechkula.deepskyapp.data.repository

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.core.common.Logger
import com.wojciechkula.deepskyapp.core.common.logDescription
import com.wojciechkula.deepskyapp.data.api.APODApi
import com.wojciechkula.deepskyapp.data.api.dto.PictureOfTheDayDto
import com.wojciechkula.deepskyapp.data.mapper.toDomain
import com.wojciechkula.deepskyapp.domain.Result
import com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import io.ktor.client.call.body
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.http.isSuccess
import kotlinx.io.IOException

private const val TAG = "PictureRepository"

internal class PictureRepositoryImpl(
    private val api: APODApi,
    private val dateFormatter: DateFormatter,
    private val logger: Logger
) : PictureRepository {

    override suspend fun getPictureOfTheDay(): Result<PictureOfTheDayModel> =
        try {
            val response = api.getPictureOfTheDay(dateFormatter.currentApodDate())
            if (response.status.isSuccess()) {
                Result.Success(response.body<PictureOfTheDayDto>().toDomain())
            } else {
                // Both failure branches map to the same Error state in the UI, so without a log line a
                // rejected key is indistinguishable from a dead network.
                logger.e(TAG, "APOD request failed with HTTP ${response.status.value}")
                Result.HttpError(response.status.value, response.status.description)
            }
            // Catching Throwable is the point: a repository turns *any* transport failure into a Result the
            // caller can render, and the engines differ in what they throw (OkHttp wraps, Darwin does not).
        } catch (@Suppress("TooGenericExceptionCaught") throwable: Throwable) {
            logger.e(TAG, "APOD request failed: ${throwable.logDescription()}")
            when {
                throwable.isServerNotResponding() -> Result.ServerNotResponding
                throwable.isConnectionFailure() -> Result.NetworkError
                else -> Result.Exception(throwable)
            }
        }

    private fun Throwable.causeChain(): Sequence<Throwable> = generateSequence(this) { it.cause }

    private fun Throwable.isConnectionFailure(): Boolean = causeChain().any { it is IOException }

    // A connected request that got no answer. OkHttp wraps a connect timeout's SocketTimeoutException in a
    // ConnectTimeoutException, so that one must be ruled out first; Darwin reports every timeout as a socket one.
    private fun Throwable.isServerNotResponding(): Boolean =
        causeChain().none { it is ConnectTimeoutException } &&
            causeChain().any { it is HttpRequestTimeoutException || it is SocketTimeoutException }
}
