package com.wojciechkula.deepskyapp.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModules())
    }
}
