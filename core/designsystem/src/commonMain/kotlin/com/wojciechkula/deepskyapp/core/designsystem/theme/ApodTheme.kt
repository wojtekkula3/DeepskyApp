package com.wojciechkula.deepskyapp.core.designsystem.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.tappableElement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.unit.Dp

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

    /**
     * How far the floating navigation card is lifted off the bottom of the window — the larger of two
     * lower bounds, because a bottom inset alone does not say what is *inside* it:
     *
     * - a per-platform minimum that clears the system's home indicator. It is not the safe area: iOS's
     *   34pt inset is mostly empty space around a thin indicator, so the card may sit inside it;
     * - opaque chrome plus [ApodDimensions.marginMedium]. Where the inset is filled by something the
     *   system both draws and takes taps in — Android's three-button bar — the gap has to be added on
     *   top of it. `tappableElement` is what tells the two apart: it is the whole bar in three-button
     *   mode, and zero under gesture navigation and on iOS.
     */
    val floatingNavigationBottomMargin: Dp
        @Composable
        get() {
            val opaqueChrome = WindowInsets.tappableElement.asPaddingValues().calculateBottomPadding()
            return maxOf(FloatingNavigationMinBottomMargin, opaqueChrome + ApodDimensions.marginMedium)
        }

    /**
     * What a screen under the floating navigation leaves clear at the end of its scrollable content,
     * measured from where its viewport ends — which is the three-button bar's top edge when there is
     * one, hence the `tappableElement` subtraction.
     */
    val floatingNavigationSpace: Dp
        @Composable
        get() = floatingNavigationBottomMargin +
            ApodDimensions.floatingNavigationHeight +
            ApodDimensions.marginMedium -
            WindowInsets.tappableElement.asPaddingValues().calculateBottomPadding()
}

// `extraSmall` and `extraLarge` are left at the Material baseline: the design names no token for
// them, and its `navigation` radius happens to equal the baseline `extraLarge` already.
private val ApodMaterialShapes = Shapes(
    small = ApodShapes.small,
    medium = ApodShapes.medium,
    large = ApodShapes.large
)
