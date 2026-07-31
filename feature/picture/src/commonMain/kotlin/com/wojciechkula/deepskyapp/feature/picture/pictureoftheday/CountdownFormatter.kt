package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Instant

// APOD publishes a new picture at 00:00 in the GMT-4 zone (matches the original app).
private val ApodZone: TimeZone = TimeZone.of("UTC-04:00")

internal fun formatTimeToNextApod(now: Instant): String {
    val today = now.toLocalDateTime(ApodZone).date
    val nextMidnight = today.plus(1, DateTimeUnit.DAY).atStartOfDayIn(ApodZone)
    val remaining = nextMidnight - now
    val hours = remaining.inWholeHours
    val minutes = remaining.inWholeMinutes % 60
    val seconds = remaining.inWholeSeconds % 60
    return "${hours}h ${minutes}min ${seconds}s"
}
