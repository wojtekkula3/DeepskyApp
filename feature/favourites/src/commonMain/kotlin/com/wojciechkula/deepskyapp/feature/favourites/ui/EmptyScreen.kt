package com.wojciechkula.deepskyapp.feature.favourites.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite_border
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.feature.favourites.resources.Res
import com.wojciechkula.deepskyapp.feature.favourites.resources.favourites_empty
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun EmptyScreen(
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ScreenTitle(onAboutClick = onAboutClick)
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = ApodTheme.dimensions.marginLarge,
                    top = ApodTheme.dimensions.marginLarge,
                    end = ApodTheme.dimensions.marginLarge
                ),
            shape = ApodTheme.shapes.large,
            color = ApodTheme.colors.surface,
            border = BorderStroke(ApodTheme.dimensions.borderSmall, ApodTheme.colors.outline),
            shadowElevation = ApodTheme.elevation.detailsCard
        ) {
            Column(
                modifier = Modifier.padding(ApodTheme.dimensions.marginLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                EmptyIllustration()
                Text(
                    text = stringResource(Res.string.favourites_empty),
                    modifier = Modifier.padding(top = ApodTheme.dimensions.marginMedium),
                    style = ApodTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = ApodTheme.colors.onSurface
                )
            }
        }
    }
}

@Composable
private fun EmptyIllustration() {
    Box(
        modifier = Modifier
            .size(ApodTheme.dimensions.illustrationLarge)
            .background(ApodTheme.colors.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(DesignSystemRes.drawable.ic_favourite_border),
            contentDescription = null,
            modifier = Modifier.size(ApodTheme.dimensions.iconExtraLarge),
            tint = ApodTheme.colors.primary
        )
    }
}

@LightDarkPreview
@Composable
private fun EmptyScreenPreview() = ApodColumnPreview {
    EmptyScreen(onAboutClick = {}, modifier = Modifier.fillMaxSize())
}
