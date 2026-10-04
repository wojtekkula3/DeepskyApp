package com.wojciechkula.deepskyapp.core.common

import android.content.Context
import android.os.Bundle
import com.google.firebase.Firebase
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics

/**
 * Firebase Analytics when the build had a google-services.json, otherwise [NoOpAnalytics]: a clone of the
 * public repository builds without it, and `Firebase.analytics` throws when no Firebase app was initialised.
 */
fun androidAnalytics(context: Context): Analytics =
    if (FirebaseApp.getApps(context).isEmpty()) NoOpAnalytics() else FirebaseAnalyticsTracker()

private class FirebaseAnalyticsTracker : Analytics {
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
