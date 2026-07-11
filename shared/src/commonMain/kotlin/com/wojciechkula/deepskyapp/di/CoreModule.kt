package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModule: Module = module {
    single { DateFormatter() }
}
