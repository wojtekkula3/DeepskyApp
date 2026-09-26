package com.wojciechkula.deepskyapp.core.designsystem.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme

@Composable
fun ContentCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = ApodTheme.dimensions.marginLarge),
        shape = ApodTheme.shapes.large,
        color = ApodTheme.colors.surface,
        border = BorderStroke(ApodTheme.dimensions.borderSmall, ApodTheme.colors.outline),
        shadowElevation = ApodTheme.elevation.detailsCard,
        content = content
    )
}

@LightDarkPreview
@Composable
private fun ContentCardPreview() = ApodColumnPreview(
    modifier = Modifier.padding(vertical = ApodTheme.dimensions.marginMedium)
) {
    ContentCard {
        Text(
            text = "Card content",
            modifier = Modifier.padding(ApodTheme.dimensions.marginMedium),
            style = ApodTheme.typography.bodyLarge
        )
    }
}
