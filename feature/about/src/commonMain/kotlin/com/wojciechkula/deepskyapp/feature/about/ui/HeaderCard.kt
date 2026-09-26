package com.wojciechkula.deepskyapp.feature.about.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.ContentCard
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.feature.about.resources.Res
import com.wojciechkula.deepskyapp.feature.about.resources.about_content_description_apod_logo
import com.wojciechkula.deepskyapp.feature.about.resources.about_content_description_nasa_logo
import com.wojciechkula.deepskyapp.feature.about.resources.about_powered_by
import com.wojciechkula.deepskyapp.feature.about.resources.about_title
import com.wojciechkula.deepskyapp.feature.about.resources.apod_logo
import com.wojciechkula.deepskyapp.feature.about.resources.nasa_logo
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun HeaderCard(modifier: Modifier = Modifier) {
    val colors = ApodTheme.colors
    ContentCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .background(Brush.linearGradient(listOf(colors.surface, colors.surfaceVariant)))
                .drawBehind { drawMoon(disc = colors.primaryContainer, shade = colors.onSurfaceVariant) }
                .padding(ApodTheme.dimensions.marginLarge)
        ) {
            Text(
                text = stringResource(Res.string.about_title),
                style = ApodTheme.typography.headlineLarge,
                color = ApodTheme.colors.onSurface
            )
            Text(
                text = stringResource(Res.string.about_powered_by),
                modifier = Modifier.padding(top = ApodTheme.dimensions.marginSmall),
                style = ApodTheme.typography.bodyMedium,
                color = ApodTheme.colors.onSurfaceVariant
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = ApodTheme.dimensions.marginSmall),
                horizontalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginLarge, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.nasa_logo),
                    contentDescription = stringResource(Res.string.about_content_description_nasa_logo),
                    modifier = Modifier.size(ApodTheme.dimensions.illustrationXLarge)
                )
                Image(
                    painter = painterResource(Res.drawable.apod_logo),
                    contentDescription = stringResource(Res.string.about_content_description_apod_logo),
                    modifier = Modifier.size(ApodTheme.dimensions.illustrationLarge)
                )
            }
        }
    }
}

private fun DrawScope.drawMoon(disc: Color, shade: Color) {
    val center = Offset(size.width * MOON_CENTER_X, size.height * MOON_CENTER_Y)
    val radius = size.width * MOON_RADIUS
    drawCircle(
        brush = Brush.radialGradient(listOf(disc, Color.Transparent), center, radius * MOON_GLOW_SCALE),
        radius = radius * MOON_GLOW_SCALE,
        center = center
    )
    drawCircle(color = disc, radius = radius, center = center)
    // Lit from the lower left, so the shadow gathers on the upper-right rim.
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color.Transparent, shade.copy(alpha = MOON_SHADOW_ALPHA)),
            center = center + Offset(-radius * MOON_LIGHT_OFFSET, radius * MOON_LIGHT_OFFSET),
            radius = radius * MOON_SHADOW_SCALE
        ),
        radius = radius,
        center = center
    )
    MoonCraters.forEach { (x, y, r) ->
        drawCircle(
            color = shade.copy(alpha = MOON_CRATER_ALPHA),
            radius = radius * r,
            center = center + Offset(radius * x, radius * y)
        )
    }
}

// Fractions of the card width/height, so the moon keeps its place in the corner at any screen width.
private const val MOON_CENTER_X = 0.92f
private const val MOON_CENTER_Y = 0.08f
private const val MOON_RADIUS = 0.24f
private const val MOON_GLOW_SCALE = 1.3f
private const val MOON_LIGHT_OFFSET = 0.5f
private const val MOON_SHADOW_SCALE = 1.9f
private const val MOON_SHADOW_ALPHA = 0.35f
private const val MOON_CRATER_ALPHA = 0.12f

// Offset x, offset y and radius, each a fraction of the moon radius.
private val MoonCraters = listOf(
    Triple(-0.45f, 0.35f, 0.18f),
    Triple(-0.1f, 0.62f, 0.1f),
    Triple(-0.62f, -0.05f, 0.09f),
    Triple(-0.25f, 0.15f, 0.07f),
    Triple(0.2f, 0.45f, 0.13f)
)

@LightDarkPreview
@Composable
private fun HeaderCardPreview() = ApodColumnPreview(
    modifier = Modifier.padding(vertical = ApodTheme.dimensions.marginMedium)
) {
    HeaderCard()
}
