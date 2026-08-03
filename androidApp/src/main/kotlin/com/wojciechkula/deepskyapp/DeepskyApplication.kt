package com.wojciechkula.deepskyapp

import android.app.Application
import android.content.pm.ApplicationInfo
import com.wojciechkula.deepskyapp.di.initKoin
import timber.log.Timber

class DeepskyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Without a planted tree Timber discards every log, so the Logger abstraction is inert. The
        // debuggable flag is read instead of BuildConfig.DEBUG so :androidApp does not have to
        // re-enable buildConfig, which M5 removed when the APOD key moved into :shared.
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Timber.plant(Timber.DebugTree())
        }
        initKoin(context = this)
    }
}
