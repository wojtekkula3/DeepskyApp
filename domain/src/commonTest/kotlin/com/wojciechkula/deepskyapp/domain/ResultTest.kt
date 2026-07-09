package com.wojciechkula.deepskyapp.domain

import kotlin.test.Test
import kotlin.test.assertEquals

class ResultTest {

    @Test
    fun success_carriesData() {
        val result: Result<Int> = Result.Success(42)
        assertEquals(42, (result as Result.Success).data)
    }

    @Test
    fun httpError_carriesCodeAndMessage() {
        val result: Result<Int> = Result.HttpError(404, "Not Found")
        result as Result.HttpError
        assertEquals(404, result.code)
        assertEquals("Not Found", result.message)
    }

    @Test
    fun exception_carriesThrowable() {
        val cause = RuntimeException("boom")
        val result: Result<Int> = Result.Exception(cause)
        assertEquals(cause, (result as Result.Exception).throwable)
    }
}
