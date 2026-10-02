package com.wojciechkula.deepskyapp.data.repository

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import com.wojciechkula.deepskyapp.core.common.Logger
import com.wojciechkula.deepskyapp.data.api.APODApi
import com.wojciechkula.deepskyapp.domain.Result
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandleScope
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.HttpRequestData
import io.ktor.client.request.HttpResponseData
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.Url
import io.ktor.http.headersOf
import io.ktor.serialization.kotlinx.json.json
import kotlin.coroutines.cancellation.CancellationException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Clock
import kotlin.time.Instant
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlinx.serialization.json.Json

// 2026-07-09 15:00 at GMT-4, the zone DateFormatter resolves the APOD date in.
private object FixedClock : Clock {
    override fun now(): Instant = Instant.parse("2026-07-09T19:00:00Z")
}

private class FakeLogger : Logger {
    val errors = mutableListOf<String>()

    override fun d(tag: String, message: String) = Unit

    override fun e(tag: String, message: String) {
        errors += message
    }
}

private val JSON_BODY = """
    {
      "date": "2026-07-09",
      "post_id": 1,
      "title": "Galaxy",
      "permalink": "https://science.nasa.gov/image-article/apod-2026-july-9-galaxy/",
      "media_type": "image",
      "explanation": "<strong>Explanation:</strong> A distant galaxy.",
      "credit": "NASA",
      "copyright": "NASA",
      "alt": "A galaxy.",
      "url": "https://science.nasa.gov/image-article/apod-2026-july-9-galaxy/",
      "hdurl": "https://example.com/hd.jpg",
      "basic_html": "<html></html>",
      "basic_html_url": "https://science.nasa.gov/wp-json/wp/v2/apod-basic/260709/html"
    }
""".trimIndent()

class PictureRepositoryImplTest {

    private fun repository(
        logger: Logger = FakeLogger(),
        handler: suspend MockRequestHandleScope.(HttpRequestData) -> HttpResponseData,
    ): PictureRepositoryImpl {
        val client = HttpClient(MockEngine { request -> handler(request) }) {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
        val api = APODApi(client = client)
        return PictureRepositoryImpl(api, DateFormatter(FixedClock), logger)
    }

    @Test
    fun `maps a successful response to Result_Success with the media taken from hdurl`() = runTest {
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
        assertEquals("https://example.com/hd.jpg", picture.url)
        assertEquals("https://example.com/hd.jpg", picture.hdUrl)
        assertEquals("image", picture.mediaType)
        assertEquals("A distant galaxy.", picture.explanation)
        assertEquals("NASA", picture.copyright)
    }

    @Test
    fun `requests the APOD date by its legacy code without an api key`() = runTest {
        var capturedUrl: Url? = null
        val repository = repository { request ->
            capturedUrl = request.url
            respond(
                content = JSON_BODY,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        repository.getPictureOfTheDay()

        val url = assertNotNull(capturedUrl)
        assertEquals("https://science.nasa.gov/wp-json/wp/v2/apod-basic/260709", url.toString())
        assertTrue(url.parameters.isEmpty())
    }

    @Test
    fun `requests a given date by its legacy code`() = runTest {
        var capturedPath: String? = null
        val repository = repository { request ->
            capturedPath = request.url.encodedPath
            respond(
                content = JSON_BODY,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, "application/json"),
            )
        }

        repository.getPicture("2014-09-18")

        assertEquals("/wp-json/wp/v2/apod-basic/140918", capturedPath)
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

    @Test
    fun `maps a connection failure to Result_NetworkError`() = runTest {
        val repository = repository { throw IOException("connection refused") }

        val result = repository.getPictureOfTheDay()

        assertEquals(Result.NetworkError, result)
    }

    @Test
    fun `maps a request timeout to Result_ServerNotResponding`() = runTest {
        val repository = repository { throw HttpRequestTimeoutException("https://api.nasa.gov", 30_000L) }

        val result = repository.getPictureOfTheDay()

        assertEquals(Result.ServerNotResponding, result)
    }

    @Test
    fun `maps a socket timeout to Result_ServerNotResponding`() = runTest {
        val repository = repository { throw SocketTimeoutException("read timed out") }

        val result = repository.getPictureOfTheDay()

        assertEquals(Result.ServerNotResponding, result)
    }

    @Test
    fun `maps a connect timeout to Result_NetworkError even though its cause is a socket timeout`() = runTest {
        val repository = repository {
            throw ConnectTimeoutException("Connect timeout has expired", SocketTimeoutException("connect timed out"))
        }

        val result = repository.getPictureOfTheDay()

        assertEquals(Result.NetworkError, result)
    }

    @Test
    fun `logs the status code when the API answers with an error`() = runTest {
        val logger = FakeLogger()
        val repository = repository(logger) { respondError(HttpStatusCode.Forbidden) }

        repository.getPictureOfTheDay()

        assertTrue(logger.errors.single().contains("403"))
    }

    @Test
    fun `logs a failed request by its type only without the raw message`() = runTest {
        val logger = FakeLogger()
        val repository = repository(logger) {
            throw RuntimeException("connect failed: /wp-json/wp/v2/apod-basic/260709")
        }

        repository.getPictureOfTheDay()

        val line = logger.errors.single()
        assertTrue(line.contains("RuntimeException"))
        assertFalse(line.contains("connect failed"))
    }

    @Test
    fun `rethrows cancellation instead of turning it into a result`() = runTest {
        val logger = FakeLogger()
        val repository = repository(logger) { throw CancellationException("left the screen") }

        assertFailsWith<CancellationException> { repository.getPicture("2026-08-24") }
        assertTrue(logger.errors.isEmpty())
    }
}
