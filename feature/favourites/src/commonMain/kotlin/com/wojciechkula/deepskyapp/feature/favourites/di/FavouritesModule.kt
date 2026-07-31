package com.wojciechkula.deepskyapp.feature.favourites.di

import com.wojciechkula.deepskyapp.feature.favourites.FavouritesViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val favouritesModule: Module = module {
    viewModel { FavouritesViewModel(get()) }
}
