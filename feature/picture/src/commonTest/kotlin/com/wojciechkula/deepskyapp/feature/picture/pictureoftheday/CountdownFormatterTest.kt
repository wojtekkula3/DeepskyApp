package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Instant

class CountdownFormatterTest {

    @Test
    fun formatsRemainingTimeToNextApodMidnightAtUtcMinus4() {
        // 2026-07-12T20:00:00Z == 2026-07-12T16:00:00 at UTC-4.
        // Next APOD update is 2026-07-13T00:00:00 at UTC-4 == 2026-07-13T04:00:00Z.
        // Remaining = 8h 0min 0s.
        val now = Instant.parse("2026-07-12T20:00:00Z")
        assertEquals("8h 0min 0s", formatTimeToNextApod(now))
    }

    @Test
    fun formatsSubHourRemainder() {
        // 2026-07-13T03:30:15Z == remaining 29min 45s to 04:00:00Z.
        val now = Instant.parse("2026-07-13T03:30:15Z")
        assertEquals("0h 29min 45s", formatTimeToNextApod(now))
    }
}
