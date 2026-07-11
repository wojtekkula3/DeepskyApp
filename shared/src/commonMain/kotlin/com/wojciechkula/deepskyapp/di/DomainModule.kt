package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.domain.interactor.AddFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.CheckIfPictureIsFavouriteInteractor
import com.wojciechkula.deepskyapp.domain.interactor.DeleteFavouritePictureInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetFavouritePicturesInteractor
import com.wojciechkula.deepskyapp.domain.interactor.GetPictureOfTheDayInteractor
import org.koin.core.module.Module
import org.koin.dsl.module

val domainModule: Module = module {
    factory { GetPictureOfTheDayInteractor(get()) }
    factory { GetFavouritePicturesInteractor(get()) }
    factory { AddFavouritePictureInteractor(get()) }
    factory { DeleteFavouritePictureInteractor(get()) }
    factory { CheckIfPictureIsFavouriteInteractor(get(), get()) }
}
