package com.wojciechkula.deepskyapp.core.common

import platform.Foundation.NSLog

class IosLogger : Logger {
    override fun d(
        tag: String,
        message: String
    ) {
        NSLog("D/%@: %@", tag, message)
    }

    override fun e(
        tag: String,
        message: String
    ) {
        NSLog("E/%@: %@", tag, message)
    }
}
