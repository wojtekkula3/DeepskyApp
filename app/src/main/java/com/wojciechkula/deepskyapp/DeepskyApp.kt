package com.wojciechkula.deepskyapp

import android.app.Application
import com.wojciechkula.deepskyapp.initializer.AppInitializersContainer
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class DeepskyApp : Application() {

    @Inject
    lateinit var initializersContainer: AppInitializersContainer

    override fun onCreate() {
        super.onCreate()
        initializersContainer.init(this)
    }
}