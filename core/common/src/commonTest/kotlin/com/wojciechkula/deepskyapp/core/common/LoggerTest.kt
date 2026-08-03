package com.wojciechkula.deepskyapp.core.common

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

class LoggerTest {

    @Test
    fun `log description names the exception type`() {
        assertEquals("IllegalStateException", IllegalStateException("boom").logDescription())
    }

    @Test
    fun `log description never leaks the message because it can carry the api key`() {
        val throwable =
            RuntimeException("Fail to connect to https://api.nasa.gov/planetary/apod?api_key=SECRET")

        val description = throwable.logDescription()

        assertFalse(description.contains("SECRET"))
        assertFalse(description.contains("api_key"))
    }
}
