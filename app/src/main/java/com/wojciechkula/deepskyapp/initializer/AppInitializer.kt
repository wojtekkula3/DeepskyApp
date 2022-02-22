package com.wojciechkula.deepskyapp.initializer

import android.app.Application

interface AppInitializer {
    fun init(application: Application)
}