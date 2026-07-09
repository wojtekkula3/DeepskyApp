package com.wojciechkula.deepskyapp.core.common

import timber.log.Timber

actual object Logger {
    actual fun d(tag: String, message: String) {
        Timber.tag(tag).d(message)
    }

    actual fun e(tag: String, message: String, throwable: Throwable?) {
        Timber.tag(tag).e(throwable, message)
    }
}
