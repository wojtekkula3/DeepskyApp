package com.wojciechkula.deepskyapp.feature.picture

private const val MILLIS_PER_SECOND = 1000
private const val SECONDS_PER_MINUTE = 60
private const val SECONDS_PER_HOUR = 3600
private const val TWO_DIGITS = 2

internal fun formatPlaybackTime(millis: Long): String {
    val totalSeconds = (millis / MILLIS_PER_SECOND).coerceAtLeast(0L)
    val seconds = totalSeconds % SECONDS_PER_MINUTE
    val minutes = totalSeconds / SECONDS_PER_MINUTE % SECONDS_PER_MINUTE
    val hours = totalSeconds / SECONDS_PER_HOUR
    return if (hours > 0L) {
        "$hours:${minutes.zeroPadded()}:${seconds.zeroPadded()}"
    } else {
        "$minutes:${seconds.zeroPadded()}"
    }
}

/** `0` means the platform has not reported a duration yet, which leaves the bar at its start. */
internal fun playbackProgress(positionMillis: Long, durationMillis: Long): Float =
    if (durationMillis <= 0L) 0f else (positionMillis.toFloat() / durationMillis).coerceIn(0f, 1f)

private fun Long.zeroPadded(): String = toString().padStart(TWO_DIGITS, '0')
