package com.wojciechkula.deepskyapp.data.di

import com.wojciechkula.deepskyapp.data.api.APODApi
import com.wojciechkula.deepskyapp.data.network.createHttpClient
import com.wojciechkula.deepskyapp.data.network.httpClientEngine
import com.wojciechkula.deepskyapp.data.repository.FavouriteRepositoryImpl
import com.wojciechkula.deepskyapp.data.repository.PictureRepositoryImpl
import com.wojciechkula.deepskyapp.domain.repository.FavouriteRepository
import com.wojciechkula.deepskyapp.domain.repository.PictureRepository
import org.koin.core.module.Module
import org.koin.dsl.module

// Platform-specific database + DAO (Android needs a Context; iOS does not).
expect fun platformModule(): Module

fun dataModule(apiKey: String): Module = module {
    single { createHttpClient(httpClientEngine()) }
    single { APODApi(client = get(), apiKey = apiKey) }
    single<PictureRepository> {
        PictureRepositoryImpl(api = get(), dateFormatter = get(), logger = get())
    }
    single<FavouriteRepository> { FavouriteRepositoryImpl(dao = get()) }
}
