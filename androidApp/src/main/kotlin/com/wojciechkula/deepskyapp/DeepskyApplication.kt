package com.wojciechkula.deepskyapp

import android.app.Application
import android.content.pm.ApplicationInfo
import com.wojciechkula.deepskyapp.di.initKoin
import timber.log.Timber

class DeepskyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        if (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE != 0) {
            Timber.plant(Timber.DebugTree())
        }
        initKoin(context = this)
    }
}
