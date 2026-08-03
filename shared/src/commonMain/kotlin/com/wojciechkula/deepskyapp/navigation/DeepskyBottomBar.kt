package com.wojciechkula.deepskyapp.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.tooling.preview.Preview
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_image
import com.wojciechkula.deepskyapp.core.designsystem.theme.DeepskyTheme
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DeepskyBottomBar(
    modifier: Modifier = Modifier,
    selectedTab: TopLevelTab,
    onTabSelected: (TopLevelTab) -> Unit
) {
    NavigationBar(modifier = modifier) {
        TopLevelTab.entries.forEach { tab ->
            val label = stringResource(tab.label)
            NavigationBarItem(
                selected = tab == selectedTab,
                onClick = { onTabSelected(tab) },
                icon = { Icon(painter = tab.painter(), contentDescription = label) },
                label = { Text(label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = selectedTab.selectedColor(),
                    selectedTextColor = selectedTab.selectedColor(),
                    indicatorColor = Color.Transparent,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}

@Composable
private fun TopLevelTab.painter(): Painter = when (this) {
    TopLevelTab.PictureOfTheDay -> painterResource(DesignSystemRes.drawable.ic_image)
    TopLevelTab.Favourites -> painterResource(DesignSystemRes.drawable.ic_favourite)
}

// The original app swapped the bottom bar's tint per tab (blue on Picture of The Day, red on My
// favourite). Reproduced from the Material scheme instead of the old hex values so the bar also
// works in the dark scheme.
@Composable
private fun TopLevelTab.selectedColor(): Color = when (this) {
    TopLevelTab.PictureOfTheDay -> MaterialTheme.colorScheme.primary
    TopLevelTab.Favourites -> MaterialTheme.colorScheme.error
}

@Preview
@Composable
private fun DeepskyBottomBarPictureOfTheDayPreview() {
    DeepskyTheme {
        DeepskyBottomBar(selectedTab = TopLevelTab.PictureOfTheDay, onTabSelected = {})
    }
}

@Preview
@Composable
private fun DeepskyBottomBarFavouritesPreview() {
    DeepskyTheme {
        DeepskyBottomBar(selectedTab = TopLevelTab.Favourites, onTabSelected = {})
    }
}
