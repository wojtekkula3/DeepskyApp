package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import kotlin.time.Instant
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime

// APOD publishes a new picture at 00:00 in the GMT-4 zone (matches the original app).
private val ApodZone: TimeZone = TimeZone.of("UTC-04:00")

private const val SECONDS_PER_MINUTE = 60
private const val MINUTES_PER_HOUR = 60

internal fun formatTimeToNextApod(now: Instant): String {
    val today = now.toLocalDateTime(ApodZone).date
    val nextMidnight = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(ApodZone)
    val remaining = nextMidnight - now
    val hours = remaining.inWholeHours
    val minutes = remaining.inWholeMinutes % MINUTES_PER_HOUR
    val seconds = remaining.inWholeSeconds % SECONDS_PER_MINUTE
    return "${hours}h ${minutes}min ${seconds}s"
}
