package com.wojciechkula.deepskyapp.feature.picture.di

import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsAnalyticsStateHandler
import com.wojciechkula.deepskyapp.feature.picture.picturedetails.PictureDetailsViewModel
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayAnalyticsStateHandler
import com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.PictureOfTheDayViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val pictureModule: Module = module {
    // Not single: a handler remembers the last logged screen, so each ViewModel needs its own.
    factory { PictureOfTheDayAnalyticsStateHandler(get()) }
    factory { PictureDetailsAnalyticsStateHandler(get()) }
    viewModel { PictureOfTheDayViewModel(get(), get(), get(), get(), get(), get(), get(), get()) }
    viewModel { (date: String) -> PictureDetailsViewModel(date, get(), get(), get(), get()) }
}
