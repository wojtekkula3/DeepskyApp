package com.wojciechkula.deepskyapp.di

import com.wojciechkula.deepskyapp.data.di.dataModule
import com.wojciechkula.deepskyapp.data.di.platformModule
import com.wojciechkula.deepskyapp.feature.favourites.di.favouritesModule
import com.wojciechkula.deepskyapp.feature.picture.di.pictureModule
import org.koin.core.module.Module

fun appModules(): List<Module> =
    listOf(coreModule, dataModule, platformModule(), domainModule, pictureModule, favouritesModule)
