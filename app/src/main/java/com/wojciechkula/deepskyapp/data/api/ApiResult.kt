package com.wojciechkula.deepskyapp.data.api

sealed interface ApiResult<T : Any>

// Represents a network result that successfully received a response containing body data.
class ApiSuccess<T : Any>(val data: T) : ApiResult<T>

// Represents a network result that successfully received a response containing an error message.
class ApiError<T : Any>(val code: Int, val message: String?) : ApiResult<T>

//  Represents a network result that faced an unexpected exception before getting a response from the network
//  such as IOException and UnKnownHostException.
class ApiException<T : Any>(val e: Throwable) : ApiResult<T>