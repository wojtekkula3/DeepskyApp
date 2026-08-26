package com.wojciechkula.deepskyapp.core.designsystem.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ApodColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val surfaceSelected: Color,
    /**
     * The floating navigation card. It is its own role because the two themes need opposite steps:
     * light wants plain white *above* the off-white background, dark wants a surface lighter than
     * both `background` and `surface` so the card separates from the page. No single existing role
     * does both.
     */
    val navigationSurface: Color,
    val outline: Color,
    val favorite: Color,
    val onFavorite: Color,
    val error: Color,
    val onError: Color,
    val scrim: Color
)

private val White = Color(0xFFFFFFFF)

// Roles the design does not name — onPrimaryContainer, error, onError, scrim — are read off the
// Material baseline rather than written as a hex here, so they stay recognisable as defaults.
private val LightBaseline = lightColorScheme()
private val DarkBaseline = darkColorScheme()

private val LightPrimaryContainer = Color(0xFFEAF0FF)
private val DarkSurfaceSelected = Color(0xFF2E3F6E)
private val DarkSurfaceVariant = Color(0xFF2F3759)

internal val ApodLightColors = ApodColors(
    primary = Color(0xFF4A63D6),
    onPrimary = White,
    primaryContainer = LightPrimaryContainer,
    onPrimaryContainer = LightBaseline.onPrimaryContainer,
    background = Color(0xFFF8F7FC),
    onBackground = Color(0xFF1C1B1F),
    surface = White,
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFF1EEF9),
    onSurfaceVariant = Color(0xFF5F5E60),
    surfaceSelected = LightPrimaryContainer,
    navigationSurface = White,
    outline = Color(0xFFE5E1EC),
    favorite = Color(0xFFE45868),
    onFavorite = White,
    error = LightBaseline.error,
    onError = LightBaseline.onError,
    scrim = LightBaseline.scrim
)

internal val ApodDarkColors = ApodColors(
    primary = Color(0xFF7B93F0),
    onPrimary = Color(0xFF101B33),
    primaryContainer = DarkSurfaceSelected,
    onPrimaryContainer = DarkBaseline.onPrimaryContainer,
    background = Color(0xFF1D2438),
    onBackground = Color(0xFFF2F5FF),
    surface = Color(0xFF282E4A),
    onSurface = Color(0xFFF2F5FF),
    surfaceVariant = DarkSurfaceVariant,
    onSurfaceVariant = Color(0xFFC4C9DA),
    surfaceSelected = DarkSurfaceSelected,
    navigationSurface = DarkSurfaceVariant,
    outline = Color(0xFF343B5C),
    favorite = Color(0xFFFF5B5E),
    onFavorite = White,
    error = DarkBaseline.error,
    onError = DarkBaseline.onError,
    scrim = DarkBaseline.scrim
)

private fun ApodColors.toColorScheme(baseline: ColorScheme): ColorScheme = baseline.copy(
    primary = primary,
    onPrimary = onPrimary,
    primaryContainer = primaryContainer,
    onPrimaryContainer = onPrimaryContainer,
    background = background,
    onBackground = onBackground,
    surface = surface,
    onSurface = onSurface,
    surfaceVariant = surfaceVariant,
    onSurfaceVariant = onSurfaceVariant,
    outline = outline,
    outlineVariant = outline,
    error = error,
    onError = onError,
    scrim = scrim,
    surfaceContainerLowest = background,
    surfaceContainerLow = surface,
    surfaceContainer = surface,
    surfaceContainerHigh = surfaceVariant,
    surfaceContainerHighest = surfaceVariant
)

internal val ApodLightColorScheme: ColorScheme = ApodLightColors.toColorScheme(LightBaseline)

internal val ApodDarkColorScheme: ColorScheme = ApodDarkColors.toColorScheme(DarkBaseline)

internal val LocalApodColors = staticCompositionLocalOf { ApodLightColors }
