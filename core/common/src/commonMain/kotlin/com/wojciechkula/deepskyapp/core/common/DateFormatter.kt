package com.wojciechkula.deepskyapp.core.common

import kotlin.time.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

class DateFormatter(
    private val clock: Clock = Clock.System,
    private val timeZone: TimeZone = TimeZone.currentSystemDefault(),
) {
    fun currentApodDate(): String =
        clock.now().toLocalDateTime(timeZone).date.toString()
}
