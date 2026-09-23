package com.wojciechkula.deepskyapp.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.layout
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_image
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import kotlin.math.roundToInt
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val NavigationBorderWidth = 0.5.dp
private val IconLabelSpacing = 4.dp

private val IndicatorSlideSpec = spring<Float>(stiffness = Spring.StiffnessMediumLow)

/**
 * The bottom navigation as a card floating over the screen: inset from both side edges and lifted off
 * the bottom one, so it never touches an edge.
 *
 * It is driven by [TopLevelTab.entries], so it needs no change when a tab is added or removed.
 */
@Composable
internal fun FloatingBottomNavigation(
    selectedTab: TopLevelTab,
    onTabSelected: (TopLevelTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val tabs = TopLevelTab.entries
    val indicatorPosition by animateFloatAsState(
        targetValue = selectedTab.ordinal.toFloat(),
        animationSpec = IndicatorSlideSpec,
        label = "indicatorPosition"
    )

    Surface(
        // Horizontal only: floatingNavigationBottomMargin already clears the bottom safe area.
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
            .padding(
                start = ApodTheme.dimensions.marginLarge,
                end = ApodTheme.dimensions.marginLarge,
                bottom = ApodTheme.floatingNavigationBottomMargin
            )
            .fillMaxWidth()
            .height(ApodTheme.dimensions.floatingNavigationHeight),
        shape = ApodTheme.shapes.navigation,
        color = ApodTheme.colors.navigationSurface,
        border = BorderStroke(NavigationBorderWidth, ApodTheme.colors.outline),
        shadowElevation = ApodTheme.elevation.floatingNavigation
    ) {
        Box {
            Box(
                modifier = Modifier
                    .fillMaxWidth(1f / tabs.size)
                    .fillMaxHeight()
                    // Moved at placement, so the slide re-places one box instead of recomposing the bar.
                    .layout { measurable, constraints ->
                        val placeable = measurable.measure(constraints)
                        layout(placeable.width, placeable.height) {
                            placeable.placeRelative((indicatorPosition * placeable.width).roundToInt(), 0)
                        }
                    }
                    .padding(ApodTheme.dimensions.marginSmall)
                    .background(
                        color = ApodTheme.colors.primaryContainer,
                        shape = ApodTheme.shapes.navigation
                    )
            )

            Row {
                tabs.forEach { tab ->
                    FloatingBottomNavigationItem(
                        modifier = Modifier.weight(1f),
                        tab = tab,
                        selected = tab == selectedTab,
                        onClick = { onTabSelected(tab) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FloatingBottomNavigationItem(
    modifier: Modifier = Modifier,
    tab: TopLevelTab,
    selected: Boolean,
    onClick: () -> Unit
) {
    val label = stringResource(tab.label)
    val contentColor by animateColorAsState(
        targetValue = if (selected) ApodTheme.colors.primary else ApodTheme.colors.onSurfaceVariant,
        label = "contentColor"
    )

    Column(
        // The icon has no description of its own, so the item is announced once, with its state.
        modifier = modifier
            .fillMaxHeight()
            .padding(ApodTheme.dimensions.marginSmall)
            .selectable(
                selected = selected,
                // The sliding pill is the press feedback; a ripple on top of it reads as a grey flash.
                interactionSource = null,
                indication = null,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics { contentDescription = label }
            .padding(horizontal = ApodTheme.dimensions.marginSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(IconLabelSpacing, Alignment.CenterVertically)
    ) {
        Icon(
            painter = tab.painter(),
            contentDescription = null,
            tint = contentColor
        )
        Text(
            text = label,
            style = ApodTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1
        )
    }
}

@Composable
private fun TopLevelTab.painter(): Painter = when (this) {
    TopLevelTab.PictureOfTheDay -> painterResource(DesignSystemRes.drawable.ic_image)
    TopLevelTab.Favourites -> painterResource(DesignSystemRes.drawable.ic_favourite)
}

@Composable
private fun FloatingBottomNavigationPreview(
    darkTheme: Boolean,
    selectedTab: TopLevelTab
) {
    ApodTheme(darkTheme = darkTheme) {
        Surface(color = ApodTheme.colors.background) {
            FloatingBottomNavigation(
                selectedTab = selectedTab,
                onTabSelected = {}
            )
        }
    }
}

@Preview
@Composable
private fun FloatingBottomNavigationLightPictureOfTheDayPreview() {
    FloatingBottomNavigationPreview(darkTheme = false, selectedTab = TopLevelTab.PictureOfTheDay)
}

@Preview
@Composable
private fun FloatingBottomNavigationLightFavouritesPreview() {
    FloatingBottomNavigationPreview(darkTheme = false, selectedTab = TopLevelTab.Favourites)
}

@Preview
@Composable
private fun FloatingBottomNavigationDarkPictureOfTheDayPreview() {
    FloatingBottomNavigationPreview(darkTheme = true, selectedTab = TopLevelTab.PictureOfTheDay)
}

@Preview
@Composable
private fun FloatingBottomNavigationDarkFavouritesPreview() {
    FloatingBottomNavigationPreview(darkTheme = true, selectedTab = TopLevelTab.Favourites)
}
