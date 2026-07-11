package com.wojciechkula.deepskyapp.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
data object PictureOfTheDay : NavKey

@Serializable
data class PictureDetails(val date: String) : NavKey

@Serializable
data object Favourites : NavKey

@Serializable
data object About : NavKey
