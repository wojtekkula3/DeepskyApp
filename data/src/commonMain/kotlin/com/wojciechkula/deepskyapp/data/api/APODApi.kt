package com.wojciechkula.deepskyapp.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.statement.HttpResponse

internal class APODApi(
    private val client: HttpClient,
    private val apiKey: String,
    private val baseUrl: String = "https://api.nasa.gov/",
) {
    suspend fun getPictureOfTheDay(date: String): HttpResponse =
        client.get("${baseUrl}planetary/apod") {
            parameter("api_key", apiKey)
            parameter("date", date)
        }
}
