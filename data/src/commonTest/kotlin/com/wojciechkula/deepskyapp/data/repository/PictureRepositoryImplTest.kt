package com.wojciechkula.deepskyapp.data.repository

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.data.api.APODApi
import com.wojciechkula.deepskyapp.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

private const val API_KEY = "test-key"

private val JSON_BODY = """
    {
      "copyright": "NASA",
      "date": "2026-07-09",
      "explanation": "A distant galaxy.",
      "hdurl": "https://example.com/hd.jpg",
      "media_type": "image",
      "service_version": "v1",
      "title": "Galaxy",
      "url": "https://example.com/sd.jpg"
    }
""".trimIndent()

class PictureRepositoryImplTest {

    private fun repository(
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): PictureRepositoryImpl {
        val client = HttpClient(MockEngine { request -> handler(request) }) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val api = APODApi(client = client, apiKey = API_KEY)
        return PictureRepositoryImpl(api, DateFormatter())
    }

    @Test
    fun `maps a successful response to Result_Success with snake_case fields resolved`() = runTest {
        val repository = repository {
            respond(
                content = JSON_BODY,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        val result = repository.getPictureOfTheDay()

        val success = assertIs<Result.Success<*>>(result)
        val picture = success.data as com.wojciechkula.deepskyapp.domain.model.PictureOfTheDayModel
        assertEquals("Galaxy", picture.title)
        assertEquals("https://example.com/hd.jpg", picture.hdUrl)
        assertEquals("image", picture.mediaType)
        assertEquals("v1", picture.serviceVersion)
        assertEquals("NASA", picture.copyright)
    }

    @Test
    fun `sends the api key as a query parameter`() = runTest {
        var capturedApiKey: String? = null
        val repository = repository { request ->
            capturedApiKey = request.url.parameters["api_key"]
            respond(
                content = JSON_BODY,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        repository.getPictureOfTheDay()

        assertEquals(API_KEY, capturedApiKey)
    }

    @Test
    fun `maps a non-2xx response to Result_HttpError with the status code`() = runTest {
        val repository = repository {
            respondError(HttpStatusCode.NotFound)
        }

        val result = repository.getPictureOfTheDay()

        val error = assertIs<Result.HttpError>(result)
        assertEquals(404, error.code)
    }

    @Test
    fun `maps a thrown transport error to Result_Exception`() = runTest {
        val boom = RuntimeException("network down")
        val repository = repository { throw boom }

        val result = repository.getPictureOfTheDay()

        // The Ktor engine may wrap the thrown error, so check the whole cause chain
        // rather than instance identity (which holds on Darwin but not on OkHttp).
        val exception = assertIs<Result.Exception>(result)
        val causeChain = generateSequence(exception.throwable) { it.cause }
        assertTrue(causeChain.any { it.message?.contains("network down") == true })
    }
}
