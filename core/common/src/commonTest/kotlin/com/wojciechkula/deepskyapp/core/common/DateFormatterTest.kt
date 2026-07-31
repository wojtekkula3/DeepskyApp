package com.wojciechkula.deepskyapp.core.common

import kotlinx.datetime.TimeZone
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock
import kotlin.time.Instant

class DateFormatterTest {

    @Test
    fun currentApodDate_returnsIsoDateForFixedClock() {
        val fixedClock = FixedClock(Instant.parse("2026-07-07T10:15:30Z"))
        val formatter = DateFormatter(clock = fixedClock, timeZone = TimeZone.UTC)

        assertEquals("2026-07-07", formatter.currentApodDate())
    }

    @Test
    fun currentApodDate_resolvesInApodZoneNotDeviceZone() {
        // 2026-07-13T01:00 in Warsaw (UTC+2) is still 2026-07-12T19:00 at UTC-4, so APOD is serving
        // the 12th. Using the device zone here would ask for the 13th — a date the API has no data for.
        val fixedClock = FixedClock(Instant.parse("2026-07-12T23:00:00Z"))

        assertEquals("2026-07-12", DateFormatter(clock = fixedClock).currentApodDate())
    }
}

private class FixedClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant
}
