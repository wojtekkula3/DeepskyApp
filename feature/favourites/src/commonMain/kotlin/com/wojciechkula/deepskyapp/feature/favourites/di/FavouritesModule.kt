package com.wojciechkula.deepskyapp.feature.favourites.di

import com.wojciechkula.deepskyapp.feature.favourites.FavouritesAnalyticsStateHandler
import com.wojciechkula.deepskyapp.feature.favourites.FavouritesViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favouritesModule: Module = module {
    // Not single: a handler remembers the last logged screen, so each ViewModel needs its own.
    factory { FavouritesAnalyticsStateHandler(get()) }
    viewModel { FavouritesViewModel(get(), get()) }
}
