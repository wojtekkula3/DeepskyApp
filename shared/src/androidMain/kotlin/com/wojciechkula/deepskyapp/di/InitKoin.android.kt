package com.wojciechkula.deepskyapp.di

import android.content.Context
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

fun initKoin(context: Context, apiKey: String) {
    startKoin {
        androidContext(context)
        modules(appModules(apiKey))
    }
}
