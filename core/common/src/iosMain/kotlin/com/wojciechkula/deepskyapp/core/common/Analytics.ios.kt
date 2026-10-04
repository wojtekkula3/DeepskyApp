package com.wojciechkula.deepskyapp.core.common

// Firebase is not set up on iOS yet.
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
