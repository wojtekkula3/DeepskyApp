package com.wojciechkula.deepskyapp

import coil3.ComponentRegistry
import kotlin.test.Test
import kotlin.test.assertEquals

class DeepskyComponentsTest {

    @Test
    fun deepskyComponents_registersBothFetchersInOneRegistry() {
        val registry = ComponentRegistry.Builder().deepskyComponents().build()

        // Registering them through two `components { }` calls left only the second one, which made the
        // whole thumbnail feature dead code. A count of two is what catches that regression.
        assertEquals(2, registry.fetcherFactories.size)
    }
}
