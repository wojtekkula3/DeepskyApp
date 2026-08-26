package com.wojciechkula.deepskyapp.core.designsystem.theme

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
private fun ApodThemeShowcase(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = ApodTheme.colors.background
    ) {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(ApodTheme.dimensions.marginMedium),
            verticalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginLarge)
        ) {
            ShowcaseSection(title = "Colors") { ColorSwatches() }
            ShowcaseSection(title = "Typography") { TypeSpecimens() }
            ShowcaseSection(title = "Shapes") { ShapeSamples() }
            ShowcaseSection(title = "Elevation") { ElevationSamples() }
            ShowcaseSection(title = "Spacing") { SpacingSamples() }
        }
    }
}

@Composable
private fun ShowcaseSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall)) {
        Text(
            text = title,
            style = ApodTheme.typography.headlineMedium,
            color = ApodTheme.colors.onBackground
        )
        content()
    }
}

@Composable
private fun ColorSwatches() {
    val colors = ApodTheme.colors
    Column(verticalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall)) {
        ColorSwatch(name = "primary", color = colors.primary, onColor = colors.onPrimary)
        ColorSwatch(name = "primaryContainer", color = colors.primaryContainer, onColor = colors.onPrimaryContainer)
        ColorSwatch(name = "background", color = colors.background, onColor = colors.onBackground)
        ColorSwatch(name = "surface", color = colors.surface, onColor = colors.onSurface)
        ColorSwatch(name = "surfaceVariant", color = colors.surfaceVariant, onColor = colors.onSurfaceVariant)
        ColorSwatch(name = "surfaceSelected", color = colors.surfaceSelected, onColor = colors.onSurface)
        ColorSwatch(name = "favorite", color = colors.favorite, onColor = colors.onFavorite)
        ColorSwatch(name = "outline", color = colors.outline, onColor = colors.onSurface)
        ColorSwatch(name = "error", color = colors.error, onColor = colors.onError)
    }
}

@Composable
private fun ColorSwatch(
    name: String,
    color: Color,
    onColor: Color
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = color,
        shape = ApodTheme.shapes.small,
        border = BorderStroke(1.dp, ApodTheme.colors.outline)
    ) {
        Text(
            modifier = Modifier.padding(ApodTheme.dimensions.marginSmallMedium),
            text = name,
            style = ApodTheme.typography.labelLarge,
            color = onColor
        )
    }
}

@Composable
private fun TypeSpecimens() {
    val typography = ApodTheme.typography
    Column(verticalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall)) {
        TypeSpecimen(name = "headlineMedium", style = typography.headlineMedium)
        TypeSpecimen(name = "titleLarge", style = typography.titleLarge)
        TypeSpecimen(name = "titleMedium", style = typography.titleMedium)
        TypeSpecimen(name = "titleSmall", style = typography.titleSmall)
        TypeSpecimen(name = "bodyLarge", style = typography.bodyLarge)
        TypeSpecimen(name = "bodyMedium", style = typography.bodyMedium)
        TypeSpecimen(name = "bodySmall", style = typography.bodySmall)
        TypeSpecimen(name = "labelLarge", style = typography.labelLarge)
        TypeSpecimen(name = "labelMedium", style = typography.labelMedium)
    }
}

@Composable
private fun TypeSpecimen(
    name: String,
    style: TextStyle
) {
    Text(
        text = "$name — Astronomy Picture of the Day",
        style = style,
        color = ApodTheme.colors.onBackground
    )
}

@Composable
private fun ShapeSamples() {
    val shapes = ApodTheme.shapes
    Row(horizontalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall)) {
        BoxSample(name = "small", shape = shapes.small, color = ApodTheme.colors.primaryContainer)
        BoxSample(name = "medium", shape = shapes.medium, color = ApodTheme.colors.primaryContainer)
        BoxSample(name = "large", shape = shapes.large, color = ApodTheme.colors.primaryContainer)
        BoxSample(name = "nav", shape = shapes.navigation, color = ApodTheme.colors.primaryContainer)
        BoxSample(name = "icon", shape = shapes.iconButton, color = ApodTheme.colors.primaryContainer)
    }
}

@Composable
private fun ElevationSamples() {
    val elevation = ApodTheme.elevation
    Row(horizontalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall)) {
        BoxSample(name = "details", shape = ApodTheme.shapes.medium, elevation = elevation.detailsCard)
        BoxSample(name = "gallery", shape = ApodTheme.shapes.medium, elevation = elevation.galleryCard)
        BoxSample(name = "nav", shape = ApodTheme.shapes.navigation, elevation = elevation.floatingNavigation)
        BoxSample(name = "icon", shape = ApodTheme.shapes.iconButton, elevation = elevation.iconButton)
    }
}

@Composable
private fun BoxSample(
    name: String,
    shape: Shape,
    color: Color = ApodTheme.colors.surface,
    elevation: Dp = 0.dp
) {
    Surface(
        modifier = Modifier.size(SampleBoxSize),
        color = color,
        shape = shape,
        shadowElevation = elevation
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                style = ApodTheme.typography.labelMedium,
                color = ApodTheme.colors.onSurface
            )
        }
    }
}

@Composable
private fun SpacingSamples() {
    Column(verticalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall)) {
        SpacingSample(name = "marginSmall", size = ApodTheme.dimensions.marginSmall)
        SpacingSample(name = "marginSmallMedium", size = ApodTheme.dimensions.marginSmallMedium)
        SpacingSample(name = "marginMedium", size = ApodTheme.dimensions.marginMedium)
        SpacingSample(name = "marginMediumLarge", size = ApodTheme.dimensions.marginMediumLarge)
        SpacingSample(name = "marginLarge", size = ApodTheme.dimensions.marginLarge)
        SpacingSample(name = "margin2xLarge", size = ApodTheme.dimensions.margin2xLarge)
        SpacingSample(name = "margin3xLarge", size = ApodTheme.dimensions.margin3xLarge)
    }
}

@Composable
private fun SpacingSample(
    name: String,
    size: Dp
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .width(size)
                .height(SpacingBarHeight)
                .background(
                    color = ApodTheme.colors.primary,
                    shape = ApodTheme.shapes.small
                )
        )
        Text(
            text = name,
            style = ApodTheme.typography.bodyMedium,
            color = ApodTheme.colors.onSurfaceVariant
        )
    }
}

private val SampleBoxSize = 56.dp
private val SpacingBarHeight = 8.dp

@Preview
@Composable
private fun ApodThemeShowcaseLightPreview() {
    ApodTheme(darkTheme = false) {
        ApodThemeShowcase()
    }
}

@Preview
@Composable
private fun ApodThemeShowcaseDarkPreview() {
    ApodTheme(darkTheme = true) {
        ApodThemeShowcase()
    }
}
