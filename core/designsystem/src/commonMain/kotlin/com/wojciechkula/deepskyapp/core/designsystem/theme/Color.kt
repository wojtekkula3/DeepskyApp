package com.wojciechkula.deepskyapp.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

// Ported from the old app res/values(-night)/colors.xml + themes.xml.
private val Blue500 = Color(0xFF1566C6)
private val Blue700 = Color(0xFF0D468A)
private val Teal200 = Color(0xFF03DAC5)
private val Teal700 = Color(0xFF018786)
private val Purple200 = Color(0xFFBB86FC)
private val Purple700 = Color(0xFF3700B3)
private val White = Color(0xFFFFFFFF)
private val Black = Color(0xFF000000)

val DeepskyLightColorScheme: ColorScheme = lightColorScheme(
    primary = Blue500,
    onPrimary = White,
    primaryContainer = Blue700,
    secondary = Teal200,
    onSecondary = Black,
    secondaryContainer = Teal700,
)

val DeepskyDarkColorScheme: ColorScheme = darkColorScheme(
    primary = Purple200,
    onPrimary = Black,
    primaryContainer = Purple700,
    secondary = Teal200,
    onSecondary = Black,
    secondaryContainer = Teal200,
)
