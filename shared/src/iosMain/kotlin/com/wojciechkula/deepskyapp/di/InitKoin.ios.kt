package com.wojciechkula.deepskyapp.di

import org.koin.core.context.startKoin

fun initKoin(apiKey: String) {
    startKoin {
        modules(appModules(apiKey))
    }
}
