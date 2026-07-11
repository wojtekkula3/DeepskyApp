package com.wojciechkula.deepskyapp.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class DestinationsTest {

    @Test
    fun all_destinations_are_nav_keys() {
        // Compile-time proof that every destination conforms to NavKey — assigning them to a
        // List<NavKey> fails to compile if any stops implementing NavKey. Avoids a runtime
        // `is NavKey` check, which the compiler flags as always-true.
        val destinations: List<NavKey> = listOf(
            PictureOfTheDay,
            PictureDetails("2026-07-10"),
            Favourites,
            About,
        )
        assertEquals(4, destinations.size)
    }

    @Test
    fun picture_details_serializes_and_restores_its_date() {
        val original = PictureDetails(date = "2026-07-10")
        val json = Json.encodeToString(original)
        val restored = Json.decodeFromString<PictureDetails>(json)
        assertEquals(original, restored)
        assertEquals("2026-07-10", restored.date)
    }
}
