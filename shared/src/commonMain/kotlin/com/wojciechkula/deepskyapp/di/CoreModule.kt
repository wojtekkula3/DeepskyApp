package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.core.common.DateFormatter
import kotlin.time.Clock
import org.koin.core.module.Module
import org.koin.dsl.module

val coreModule: Module = module {
    single<Clock> { Clock.System }
    single { DateFormatter(get()) }
}
