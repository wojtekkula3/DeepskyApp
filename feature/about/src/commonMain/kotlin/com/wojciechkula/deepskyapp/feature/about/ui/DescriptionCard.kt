package com.wojciechkula.deepskyapp.feature.about.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.text.style.TextAlign
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.ContentCard
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_calendar
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_group
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_sparkle
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.feature.about.resources.Res
import com.wojciechkula.deepskyapp.feature.about.resources.about_apod_authors
import com.wojciechkula.deepskyapp.feature.about.resources.about_daily_picture
import com.wojciechkula.deepskyapp.feature.about.resources.about_welcome_body
import com.wojciechkula.deepskyapp.feature.about.resources.about_welcome_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun DescriptionCard(modifier: Modifier = Modifier) {
    ContentCard(modifier = modifier) {
        Column(modifier = Modifier.padding(horizontal = ApodTheme.dimensions.marginMedium)) {
            Section(icon = painterResource(DesignSystemRes.drawable.ic_sparkle)) {
                Text(
                    text = stringResource(Res.string.about_welcome_title),
                    style = ApodTheme.typography.titleLarge,
                    color = ApodTheme.colors.onSurface
                )
                SectionText(text = stringResource(Res.string.about_welcome_body))
            }
            HorizontalDivider(color = ApodTheme.colors.outline)
            Section(icon = painterResource(DesignSystemRes.drawable.ic_calendar)) {
                SectionText(text = stringResource(Res.string.about_daily_picture))
            }
            HorizontalDivider(color = ApodTheme.colors.outline)
            Section(icon = painterResource(DesignSystemRes.drawable.ic_group)) {
                SectionText(text = stringResource(Res.string.about_apod_authors))
            }
        }
    }
}

@Composable
private fun Section(
    icon: Painter,
    content: @Composable () -> Unit
) {
    Row(
        modifier = Modifier.padding(vertical = ApodTheme.dimensions.marginMediumLarge),
        horizontalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmallMedium)
    ) {
        Box(
            modifier = Modifier
                .size(ApodTheme.dimensions.iconExtraLarge)
                .background(ApodTheme.colors.primaryContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(ApodTheme.dimensions.iconMedium),
                tint = ApodTheme.colors.onSurface
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall)) {
            content()
        }
    }
}

@Composable
private fun SectionText(text: String) {
    Text(
        text = text,
        style = ApodTheme.typography.bodyMedium,
        color = ApodTheme.colors.onSurfaceVariant,
        textAlign = TextAlign.Justify
    )
}

@LightDarkPreview
@Composable
private fun DescriptionCardPreview() = ApodColumnPreview(
    modifier = Modifier.padding(vertical = ApodTheme.dimensions.marginMedium)
) {
    DescriptionCard()
}
