package com.wojciechkula.deepskyapp.core.common

import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

class FirebaseAnalyticsTracker : Analytics {
    private val firebaseAnalytics = Firebase.analytics

    override fun logScreenView(
        screenName: String,
        screenClass: String
    ) {
        logEvent(
            FirebaseAnalytics.Event.SCREEN_VIEW,
            mapOf(
                FirebaseAnalytics.Param.SCREEN_NAME to screenName,
                FirebaseAnalytics.Param.SCREEN_CLASS to screenClass
            )
        )
    }

    override fun logEvent(
        name: String,
        parameters: Map<String, String>
    ) {
        val bundle = Bundle().apply { parameters.forEach { (key, value) -> putString(key, value) } }
        firebaseAnalytics.logEvent(name, bundle)
    }
}
