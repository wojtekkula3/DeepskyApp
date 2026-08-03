package com.wojciechkula.deepskyapp.di

import android.content.Context
import com.wojciechkula.deepskyapp.ApodApiKey
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initKoin(context: Context) {
    startKoin {
        androidContext(context)
        modules(appModules(ApodApiKey.VALUE))
    }
}
