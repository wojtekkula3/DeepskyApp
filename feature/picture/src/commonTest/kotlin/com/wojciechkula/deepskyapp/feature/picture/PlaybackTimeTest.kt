package com.wojciechkula.deepskyapp.feature.picture

import kotlin.test.Test
import kotlin.test.assertEquals

private const val TOLERANCE = 0.0001f

class PlaybackTimeTest {

    @Test
    fun formatPlaybackTime_showsZeroAtTheStart() {
        assertEquals("0:00", formatPlaybackTime(0L))
    }

    @Test
    fun formatPlaybackTime_padsSecondsToTwoDigits() {
        assertEquals("0:07", formatPlaybackTime(7_000L))
    }

    @Test
    fun formatPlaybackTime_dropsMillisecondsRatherThanRounding() {
        assertEquals("0:07", formatPlaybackTime(7_999L))
    }

    @Test
    fun formatPlaybackTime_countsMinutes() {
        assertEquals("2:05", formatPlaybackTime(125_000L))
    }

    @Test
    fun formatPlaybackTime_addsAnHourSegmentOnlyPastAnHour() {
        assertEquals("59:59", formatPlaybackTime(3_599_000L))
        assertEquals("1:00:00", formatPlaybackTime(3_600_000L))
    }

    @Test
    fun formatPlaybackTime_padsBothSegmentsPastAnHour() {
        assertEquals("1:02:03", formatPlaybackTime(3_723_000L))
    }

    @Test
    fun formatPlaybackTime_treatsANegativePositionAsTheStart() {
        assertEquals("0:00", formatPlaybackTime(-5_000L))
    }

    @Test
    fun playbackProgress_isZeroWhileTheDurationIsUnknown() {
        assertEquals(0f, playbackProgress(positionMillis = 7_000L, durationMillis = 0L), TOLERANCE)
    }

    @Test
    fun playbackProgress_isZeroAtTheStart() {
        assertEquals(0f, playbackProgress(positionMillis = 0L, durationMillis = 40_000L), TOLERANCE)
    }

    @Test
    fun playbackProgress_isAHalfInTheMiddle() {
        assertEquals(0.5f, playbackProgress(positionMillis = 20_000L, durationMillis = 40_000L), TOLERANCE)
    }

    @Test
    fun playbackProgress_isOneAtTheEnd() {
        assertEquals(1f, playbackProgress(positionMillis = 40_000L, durationMillis = 40_000L), TOLERANCE)
    }

    @Test
    fun playbackProgress_clampsAPositionReportedPastTheEnd() {
        assertEquals(1f, playbackProgress(positionMillis = 41_000L, durationMillis = 40_000L), TOLERANCE)
    }

    @Test
    fun playbackProgress_clampsANegativePosition() {
        assertEquals(0f, playbackProgress(positionMillis = -1_000L, durationMillis = 40_000L), TOLERANCE)
    }
}
