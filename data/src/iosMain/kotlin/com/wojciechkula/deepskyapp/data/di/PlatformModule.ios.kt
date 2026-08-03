package com.wojciechkula.deepskyapp.data.di

import com.wojciechkula.deepskyapp.core.common.IosLogger
import com.wojciechkula.deepskyapp.core.common.IosNetworkMonitor
import com.wojciechkula.deepskyapp.core.common.Logger
import com.wojciechkula.deepskyapp.core.common.NetworkMonitor
import com.wojciechkula.deepskyapp.data.database.APODLocalDatabase
import com.wojciechkula.deepskyapp.data.database.buildAPODDatabase
import com.wojciechkula.deepskyapp.data.database.getDatabaseBuilder
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { buildAPODDatabase(getDatabaseBuilder()) }
    single { get<APODLocalDatabase>().favouritePictureDao() }
    single<NetworkMonitor> { IosNetworkMonitor() }
    single<Logger> { IosLogger() }
}
