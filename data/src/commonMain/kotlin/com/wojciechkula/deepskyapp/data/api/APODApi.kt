package com.wojciechkula.deepskyapp.data.api

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

internal class APODApi(
    private val client: HttpClient,
    private val baseUrl: String = "https://science.nasa.gov/wp-json/wp/v2/"
) {
    suspend fun getPicture(date: String): HttpResponse = client.get("${baseUrl}apod-basic/${date.toApodDateCode()}")
}

// The API addresses a day by the legacy APOD code: 2026-09-28 -> 260928.
private fun String.toApodDateCode(): String = substring(2).replace("-", "")
