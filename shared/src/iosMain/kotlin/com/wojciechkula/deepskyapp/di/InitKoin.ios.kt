package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.ApodApiKey
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModules(ApodApiKey.VALUE))
    }
}
