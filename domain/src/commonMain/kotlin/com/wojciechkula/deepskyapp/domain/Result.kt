package com.wojciechkula.deepskyapp.domain

sealed interface Result<out D> {
    data class Success<D>(val data: D) : Result<D>
    data class HttpError(
        val code: Int,
        val message: String?
    ) : Result<Nothing>
    data class Exception(val throwable: Throwable) : Result<Nothing>
}
