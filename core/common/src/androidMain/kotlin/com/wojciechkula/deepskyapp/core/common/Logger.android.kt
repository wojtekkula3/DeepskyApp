package com.wojciechkula.deepskyapp.core.common

import timber.log.Timber

class AndroidLogger : Logger {
    override fun d(
        tag: String,
        message: String
    ) {
        Timber.tag(tag).d(message)
    }

    override fun e(
        tag: String,
        message: String
    ) {
        Timber.tag(tag).e(message)
    }
}
