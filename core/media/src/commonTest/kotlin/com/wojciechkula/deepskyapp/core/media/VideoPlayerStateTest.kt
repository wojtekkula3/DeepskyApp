package com.wojciechkula.deepskyapp.core.media

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

private const val TOLERANCE = 0.0001f

class VideoPlayerStateTest {

    @Test
    fun aspectRatioOrNull_dividesWidthByHeightForALandscapeSize() {
        val ratio = assertNotNull(aspectRatioOrNull(1920.0, 1080.0))

        assertEquals(16f / 9f, ratio, TOLERANCE)
    }

    @Test
    fun aspectRatioOrNull_dividesWidthByHeightForAPortraitSize() {
        val ratio = assertNotNull(aspectRatioOrNull(1080.0, 1920.0))

        assertEquals(9f / 16f, ratio, TOLERANCE)
    }

    @Test
    fun aspectRatioOrNull_returnsOneForASquareSize() {
        val ratio = assertNotNull(aspectRatioOrNull(512.0, 512.0))

        assertEquals(1f, ratio, TOLERANCE)
    }

    @Test
    fun aspectRatioOrNull_returnsNullForZeroWidth() {
        assertNull(aspectRatioOrNull(0.0, 1080.0))
    }

    @Test
    fun aspectRatioOrNull_returnsNullForZeroHeight() {
        assertNull(aspectRatioOrNull(1920.0, 0.0))
    }

    @Test
    fun aspectRatioOrNull_returnsNullForBothDimensionsZero() {
        assertNull(aspectRatioOrNull(0.0, 0.0))
    }

    @Test
    fun aspectRatioOrNull_returnsNullForNegativeWidth() {
        assertNull(aspectRatioOrNull(-1920.0, 1080.0))
    }

    @Test
    fun aspectRatioOrNull_returnsNullForNegativeHeight() {
        assertNull(aspectRatioOrNull(1920.0, -1080.0))
    }

    @Test
    fun aspectRatioOrNull_returnsNullForNotANumber() {
        assertNull(aspectRatioOrNull(Double.NaN, Double.NaN))
    }

    @Test
    fun seekTargetMillis_stepsForwardInsideTheClip() {
        assertEquals(12_000L, seekTargetMillis(positionMillis = 7_000L, deltaMillis = 5_000L, durationMillis = 41_000L))
    }

    @Test
    fun seekTargetMillis_stepsBackwardInsideTheClip() {
        assertEquals(2_000L, seekTargetMillis(positionMillis = 7_000L, deltaMillis = -5_000L, durationMillis = 41_000L))
    }

    @Test
    fun seekTargetMillis_clampsToTheEndOfTheClip() {
        assertEquals(41_000L, seekTargetMillis(positionMillis = 39_000L, deltaMillis = 5_000L, durationMillis = 41_000L))
    }

    @Test
    fun seekTargetMillis_clampsToTheStartOfTheClip() {
        assertEquals(0L, seekTargetMillis(positionMillis = 3_000L, deltaMillis = -5_000L, durationMillis = 41_000L))
    }

    @Test
    fun seekTargetMillis_clampsAStepLongerThanTheWholeClip() {
        assertEquals(4_000L, seekTargetMillis(positionMillis = 1_000L, deltaMillis = 30_000L, durationMillis = 4_000L))
    }

    @Test
    fun seekTargetMillis_refusesToStepForwardWithoutAKnownDuration() {
        assertEquals(7_000L, seekTargetMillis(positionMillis = 7_000L, deltaMillis = 5_000L, durationMillis = 0L))
    }

    @Test
    fun seekTargetMillis_stillStepsBackwardWithoutAKnownDuration() {
        assertEquals(2_000L, seekTargetMillis(positionMillis = 7_000L, deltaMillis = -5_000L, durationMillis = 0L))
    }

    @Test
    fun seekTargetMillis_returnsTheStartWhenAlreadyThere() {
        assertEquals(0L, seekTargetMillis(positionMillis = 0L, deltaMillis = -5_000L, durationMillis = 41_000L))
    }

    @Test
    fun hasSeekSettled_isTrueOnAnExactLanding() {
        assertTrue(hasSeekSettled(reportedMillis = 12_000L, targetMillis = 12_000L, fromMillis = 7_000L))
    }

    @Test
    fun hasSeekSettled_isFalseWhileThePlayerStillReportsWhereItWas() {
        assertFalse(hasSeekSettled(reportedMillis = 7_000L, targetMillis = 12_000L, fromMillis = 7_000L))
    }

    @Test
    fun hasSeekSettled_toleratesLandingJustShortOfAForwardTarget() {
        assertTrue(hasSeekSettled(reportedMillis = 11_700L, targetMillis = 12_000L, fromMillis = 7_000L))
    }

    @Test
    fun hasSeekSettled_isFalseWhileStillAheadOfABackwardTarget() {
        assertFalse(hasSeekSettled(reportedMillis = 20_000L, targetMillis = 12_000L, fromMillis = 20_000L))
    }

    @Test
    fun hasSeekSettled_toleratesLandingJustPastABackwardTarget() {
        assertTrue(hasSeekSettled(reportedMillis = 12_300L, targetMillis = 12_000L, fromMillis = 20_000L))
    }

    // The reason this is a direction test and not a window test: playback keeps moving, so a poll can
    // step clean over the target. A window check would then wait for something already gone by and hold
    // the requested position for good.
    @Test
    fun hasSeekSettled_staysTrueOnceThePlayerHasRunPastAForwardTarget() {
        assertTrue(hasSeekSettled(reportedMillis = 18_000L, targetMillis = 12_000L, fromMillis = 7_000L))
    }

    @Test
    fun hasSeekSettled_staysTrueOnceThePlayerIsWellBeforeABackwardTarget() {
        assertTrue(hasSeekSettled(reportedMillis = 0L, targetMillis = 12_000L, fromMillis = 20_000L))
    }

    @Test
    fun hasSeekSettled_treatsASeekToTheSamePositionAsForward() {
        assertTrue(hasSeekSettled(reportedMillis = 12_000L, targetMillis = 12_000L, fromMillis = 12_000L))
    }
}
