package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.data.di.dataModule
import com.wojciechkula.deepskyapp.data.di.platformModule
import org.koin.core.module.Module

fun appModules(apiKey: String): List<Module> =
    listOf(coreModule, dataModule(apiKey), platformModule(), domainModule)
