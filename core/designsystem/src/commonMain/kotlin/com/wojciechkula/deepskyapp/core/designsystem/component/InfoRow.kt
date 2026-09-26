package com.wojciechkula.deepskyapp.core.designsystem.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_calendar
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun InfoRow(
    icon: Painter,
    label: String,
    value: String
) {
    Column {
        Row(
            modifier = Modifier.padding(vertical = ApodTheme.dimensions.marginSmallMedium),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(ApodTheme.dimensions.marginSmall)
        ) {
            Icon(
                painter = icon,
                contentDescription = null,
                tint = ApodTheme.colors.onSurfaceVariant
            )
            Text(
                text = label,
                style = ApodTheme.typography.bodyMediumBold,
                color = ApodTheme.colors.onSurfaceVariant
            )
            Text(
                text = value,
                modifier = Modifier.weight(1f),
                style = ApodTheme.typography.bodyMedium,
                textAlign = TextAlign.End,
                color = ApodTheme.colors.onSurface
            )
        }
        HorizontalDivider(color = ApodTheme.colors.outline)
    }
}

@LightDarkPreview
@Composable
private fun InfoRowPreview() = ApodColumnPreview(
    modifier = Modifier.padding(ApodTheme.dimensions.marginMedium)
) {
    InfoRow(
        icon = painterResource(DesignSystemRes.drawable.ic_calendar),
        label = "Date",
        value = "2026-07-12"
    )
}
