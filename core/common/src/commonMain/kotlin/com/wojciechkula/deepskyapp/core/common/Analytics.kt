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

/** Logs nothing: iOS has no Firebase yet, and an Android build without google-services.json has no Firebase app. */
class NoOpAnalytics : Analytics {
    override fun logScreenView(
        screenName: String,
        screenClass: String
    ) = Unit

    override fun logEvent(
        name: String,
        parameters: Map<String, String>
    ) = Unit
}
