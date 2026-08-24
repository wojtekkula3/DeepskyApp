package com.wojciechkula.deepskyapp.core.common

/**
 * Writes to stdout rather than `NSLog`.
 *
 * Kotlin/Native does not pass variadic arguments to a C function, so every `NSLog(format, …)` form is
 * unusable here: CFString reads whatever follows on the stack as an object and sends it `-description`,
 * which dereferences a string's own bytes as a pointer and kills the process with `EXC_BAD_ACCESS` at
 * an address made of ASCII characters. That holds for a single argument too, not just several — it was
 * measured, by shrinking the call to one argument and watching it crash the same way.
 *
 * Passing the whole line as the format with no arguments would compile and usually work, but any `%` in
 * a message — a percent-encoded URL is enough — would be read as a specifier and crash it again.
 *
 * `--console-pty` in `scripts/run-ios.sh` shows stdout on the simulator, and on a physical device
 * stdout is the only stream that is echoed at all, so nothing is lost by leaving the unified log.
 */
class IosLogger : Logger {
    override fun d(
        tag: String,
        message: String
    ) {
        println("D/$tag: $message")
    }

    override fun e(
        tag: String,
        message: String
    ) {
        println("E/$tag: $message")
    }
}
