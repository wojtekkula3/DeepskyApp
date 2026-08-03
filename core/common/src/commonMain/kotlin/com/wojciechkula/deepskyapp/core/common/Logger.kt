package com.wojciechkula.deepskyapp.core.common

/**
 * The app's logging seam. An interface rather than an `expect object` for the same reason
 * [NetworkMonitor] is one: an interface can be faked, so behaviour that logs can be asserted.
 *
 * There is deliberately no `Throwable` overload. Timber prints a throwable's message as the first line
 * of its stack trace, and a Ktor failure's message contains the full request URL — which carries the
 * APOD `api_key`. Callers pass [logDescription] instead.
 */
interface Logger {
    fun d(tag: String, message: String)
    fun e(tag: String, message: String)
}

/** The type of a failure, without its message: the message can contain the APOD `api_key`. */
fun Throwable.logDescription(): String = this::class.simpleName ?: "Throwable"
