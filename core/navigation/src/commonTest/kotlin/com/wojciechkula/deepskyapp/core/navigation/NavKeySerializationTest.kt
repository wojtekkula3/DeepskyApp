package com.wojciechkula.deepskyapp.core.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.PolymorphicSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

class NavKeySerializationTest {

    // Kotlin/Native cannot derive a serializer for an interface, so the polymorphic serializer is
    // built explicitly — exactly as NavBackStackSerializer does under the hood.
    private val serializer = ListSerializer(PolymorphicSerializer(NavKey::class))
    private val json = Json { serializersModule = NavKeySerializersModule }

    @Test
    fun every_destination_survives_a_polymorphic_round_trip() {
        val destinations: List<NavKey> = listOf(
            PictureOfTheDay,
            Favourites,
            PictureDetails("2026-07-10"),
            About,
        )

        val encoded = json.encodeToString(serializer, destinations)

        assertEquals(destinations, json.decodeFromString(serializer, encoded))
    }

    @Test
    fun saved_state_configuration_can_serialize_every_destination() {
        val module = NavKeySavedStateConfiguration.serializersModule

        assertNotNull(module.polymorphicSerializerOrNull(PictureOfTheDay))
        assertNotNull(module.polymorphicSerializerOrNull(Favourites))
        assertNotNull(module.polymorphicSerializerOrNull(PictureDetails("2026-07-10")))
        assertNotNull(module.polymorphicSerializerOrNull(About))
    }

    private fun SerializersModule.polymorphicSerializerOrNull(key: NavKey) =
        getPolymorphic(NavKey::class, key)
}
