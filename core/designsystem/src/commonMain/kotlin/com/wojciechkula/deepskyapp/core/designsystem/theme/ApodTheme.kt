package com.wojciechkula.deepskyapp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable

@Composable
fun ApodTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalApodColors provides if (darkTheme) ApodDarkColors else ApodLightColors,
        LocalApodElevation provides if (darkTheme) ApodDarkElevation else ApodLightElevation
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) ApodDarkColorScheme else ApodLightColorScheme,
            typography = apodTypography(),
            shapes = ApodMaterialShapes,
            content = content
        )
    }
}

object ApodTheme {
    val colors: ApodColors
        @Composable
        @ReadOnlyComposable
        get() = LocalApodColors.current

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val elevation: ApodElevation
        @Composable
        @ReadOnlyComposable
        get() = LocalApodElevation.current

    val dimensions: ApodDimensions get() = ApodDimensions

    val shapes: ApodShapes get() = ApodShapes
}

// `extraSmall` and `extraLarge` are left at the Material baseline: the design names no token for
// them, and its `navigation` radius happens to equal the baseline `extraLarge` already.
private val ApodMaterialShapes = Shapes(
    small = ApodShapes.small,
    medium = ApodShapes.medium,
    large = ApodShapes.large
)
