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
}

private class FixedClock(private val instant: Instant) : Clock {
    override fun now(): Instant = instant
}
