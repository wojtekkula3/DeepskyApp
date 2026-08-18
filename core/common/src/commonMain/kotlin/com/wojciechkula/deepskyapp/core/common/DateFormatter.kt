package com.wojciechkula.deepskyapp.core.common

import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// APOD publishes a new picture at 00:00 GMT-4, so "today's picture" must be resolved in that zone —
// not in the device zone. East of GMT-4 the device date rolls over hours earlier, which would ask the
// API for a date it does not serve yet and would look up favourites under the wrong date.
private val ApodTimeZone: TimeZone = TimeZone.of("UTC-04:00")

class DateFormatter(
    private val clock: Clock = Clock.System,
    private val timeZone: TimeZone = ApodTimeZone
) {
    fun currentApodDate(): String = clock.now().toLocalDateTime(timeZone).date.toString()
}
