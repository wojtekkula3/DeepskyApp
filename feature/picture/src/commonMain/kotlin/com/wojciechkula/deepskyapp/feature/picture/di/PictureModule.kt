package com.wojciechkula.deepskyapp.feature.picture.di

import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsViewModel
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val pictureModule: Module = module {
    viewModel { PictureOfTheDayViewModel(get(), get(), get(), get(), get()) }
    viewModel { (date: String) -> PictureDetailsViewModel(date, get(), get()) }
}
