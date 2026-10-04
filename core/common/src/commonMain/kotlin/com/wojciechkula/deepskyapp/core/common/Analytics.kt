package com.wojciechkula.deepskyapp.core.common

/**
 * The app's analytics seam, an interface for the same reason [Logger] is one: events can be faked and
 * asserted. Event names, parameter keys and values are what the analytics console groups by, so renaming
 * one splits its history.
 */
interface Analytics {
    /** Logs a `screen_view` for [screenName]; [screenClass] is the screen's entry composable name. */
    fun logScreenView(
        screenName: String,
        screenClass: String
    )

    /** Logs a custom event; [parameters] are forwarded unchanged. */
    fun logEvent(
        name: String,
        parameters: Map<String, String> = emptyMap()
    )
}
