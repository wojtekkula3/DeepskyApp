package com.wojciechkula.deepskyapp.core.designsystem.theme

import androidx.compose.ui.graphics.Color
import kotlin.test.Test
import kotlin.test.assertEquals

class ColorSchemeTest {

    @Test
    fun light_scheme_uses_the_ported_blue_primary() {
        assertEquals(Color(0xFF1566C6), DeepskyLightColorScheme.primary)
        assertEquals(Color(0xFFFFFFFF), DeepskyLightColorScheme.onPrimary)
    }

    @Test
    fun dark_scheme_uses_the_ported_purple_primary() {
        assertEquals(Color(0xFFBB86FC), DeepskyDarkColorScheme.primary)
        assertEquals(Color(0xFF000000), DeepskyDarkColorScheme.onPrimary)
    }
}
