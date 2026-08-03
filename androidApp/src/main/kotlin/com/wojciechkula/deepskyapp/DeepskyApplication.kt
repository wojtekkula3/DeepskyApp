package com.wojciechkula.deepskyapp

import android.app.Application
import com.wojciechkula.deepskyapp.di.initKoin

class DeepskyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(context = this)
    }
}
