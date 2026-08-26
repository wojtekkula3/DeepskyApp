package com.wojciechkula.deepskyapp.navigation

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_image
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

private val NavigationHeight = 72.dp
private val NavigationBorderWidth = 1.dp
private val IndicatorWidth = 56.dp
private val IndicatorHeight = 32.dp
private val IndicatorLabelSpacing = 4.dp
private val IconSize = 24.dp
private val MinimumTouchTargetSize = 48.dp

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
    Surface(
        // The insets come first so the margins below are measured from the safe area, not from the
        // screen edge — otherwise the card sits under the Android navigation bar or the iOS home
        // indicator.
        modifier = modifier
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .padding(
                start = ApodTheme.dimensions.marginLarge,
                end = ApodTheme.dimensions.marginLarge,
                bottom = ApodTheme.dimensions.marginMedium
            )
            .fillMaxWidth()
            .height(NavigationHeight),
        shape = ApodTheme.shapes.navigation,
        color = ApodTheme.colors.navigationSurface,
        border = BorderStroke(NavigationBorderWidth, ApodTheme.colors.outline),
        shadowElevation = ApodTheme.elevation.floatingNavigation
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TopLevelTab.entries.forEach { tab ->
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

@Composable
private fun FloatingBottomNavigationItem(
    modifier: Modifier = Modifier,
    tab: TopLevelTab,
    selected: Boolean,
    onClick: () -> Unit
) {
    val label = stringResource(tab.label)
    val contentColor by animateColorAsState(
        targetValue = if (selected) ApodTheme.colors.primary else ApodTheme.colors.onSurfaceVariant
    )
    val indicatorColor by animateColorAsState(
        targetValue = if (selected) ApodTheme.colors.primaryContainer else Color.Transparent
    )

    Column(
        // The label already names the item, so the icon carries no description of its own and the
        // whole item is announced once, with its selected state.
        modifier = modifier
            .fillMaxHeight()
            .defaultMinSize(minWidth = MinimumTouchTargetSize, minHeight = MinimumTouchTargetSize)
            .clip(ApodTheme.shapes.large)
            .selectable(
                selected = selected,
                role = Role.Tab,
                onClick = onClick
            )
            .semantics { contentDescription = label }
            .padding(horizontal = ApodTheme.dimensions.marginSmall),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(IndicatorLabelSpacing, Alignment.CenterVertically)
    ) {
        Box(
            modifier = Modifier
                .size(width = IndicatorWidth, height = IndicatorHeight)
                .background(color = indicatorColor, shape = ApodTheme.shapes.iconButton),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier.size(IconSize),
                painter = tab.painter(),
                contentDescription = null,
                tint = contentColor
            )
        }
        Text(
            text = label,
            style = ApodTheme.typography.labelMedium,
            color = contentColor,
            maxLines = 1,
            textAlign = TextAlign.Center
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
