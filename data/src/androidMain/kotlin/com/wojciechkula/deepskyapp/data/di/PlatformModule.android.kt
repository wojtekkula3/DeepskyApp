package com.wojciechkula.deepskyapp.data.di

import com.wojciechkula.deepskyapp.core.common.AndroidNetworkMonitor
import com.wojciechkula.deepskyapp.core.common.NetworkMonitor
import com.wojciechkula.deepskyapp.data.database.APODLocalDatabase
import com.wojciechkula.deepskyapp.data.database.buildAPODDatabase
import com.wojciechkula.deepskyapp.data.database.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.dsl.module

actual fun platformModule(): Module = module {
    single { buildAPODDatabase(getDatabaseBuilder(androidContext())) }
    single { get<APODLocalDatabase>().favouritePictureDao() }
    single<NetworkMonitor> { AndroidNetworkMonitor(androidContext()) }
}
