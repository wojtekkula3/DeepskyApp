package com.wojciechkula.deepskyapp.feature.picture

import com.wojciechkula.deepskyapp.core.common.Analytics

data class LoggedScreenView(val screenName: String, val screenClass: String)

data class LoggedEvent(val name: String, val parameters: Map<String, String>)

class FakeAnalytics : Analytics {
    val screenViews = mutableListOf<LoggedScreenView>()
    val events = mutableListOf<LoggedEvent>()

    override fun logScreenView(screenName: String, screenClass: String) {
        screenViews += LoggedScreenView(screenName, screenClass)
    }

    override fun logEvent(name: String, parameters: Map<String, String>) {
        events += LoggedEvent(name, parameters)
    }
}
