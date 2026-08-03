package com.wojciechkula.deepskyapp.core.navigation

import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

/**
 * Registers every [NavKey] subtype for open polymorphism. Saving and restoring a Navigation3 back
 * stack outside Android cannot fall back on reflection, so each destination has to be listed here:
 * a destination that is not registered fails at runtime when the back stack is saved.
 */
val NavKeySerializersModule: SerializersModule = SerializersModule {
    polymorphic(NavKey::class) {
        subclass(PictureOfTheDay::class)
        subclass(PictureDetails::class)
        subclass(Favourites::class)
        subclass(About::class)
    }
}

/** Configuration `rememberNavBackStack` needs to persist the back stack across process death. */
val NavKeySavedStateConfiguration: SavedStateConfiguration = SavedStateConfiguration {
    serializersModule = NavKeySerializersModule
}
