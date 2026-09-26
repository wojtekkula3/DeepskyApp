package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_cloud
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_priority_high
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_refresh
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_wifi_off
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_no_internet
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_load_error
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_try_again
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ErrorScreen(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ScreenTitle()
        ContentCard(modifier = Modifier.padding(top = ApodTheme.dimensions.marginLarge)) {
            Column(
                modifier = Modifier.padding(ApodTheme.dimensions.marginLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                ErrorIllustration()
                Text(
                    text = stringResource(Res.string.picture_of_the_day_load_error),
                    modifier = Modifier.padding(top = ApodTheme.dimensions.marginMedium),
                    style = ApodTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = ApodTheme.colors.onSurface
                )
                Text(
                    text = message,
                    modifier = Modifier.padding(top = ApodTheme.dimensions.marginSmall),
                    style = ApodTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = ApodTheme.colors.onSurfaceVariant
                )
                Button(
                    onClick = onRetry,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = ApodTheme.dimensions.marginLarge),
                    contentPadding = PaddingValues(vertical = ApodTheme.dimensions.marginMedium)
                ) {
                    Icon(
                        painter = painterResource(DesignSystemRes.drawable.ic_refresh),
                        contentDescription = null,
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                    Spacer(modifier = Modifier.width(ButtonDefaults.IconSpacing))
                    Text(
                        text = stringResource(Res.string.picture_of_the_day_try_again),
                        style = ApodTheme.typography.titleSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorIllustration() {
    Box(
        modifier = Modifier.size(ApodTheme.dimensions.illustrationXLarge),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(DesignSystemRes.drawable.ic_cloud),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            tint = ApodTheme.colors.primaryContainer
        )
        Icon(
            painter = painterResource(DesignSystemRes.drawable.ic_wifi_off),
            contentDescription = null,
            modifier = Modifier.size(ApodTheme.dimensions.iconExtraLarge),
            tint = ApodTheme.colors.primary
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = ApodTheme.dimensions.marginSmall)
                .size(ApodTheme.dimensions.iconLarge)
                .border(ApodTheme.dimensions.borderLarge, ApodTheme.colors.surface, CircleShape)
                .padding(ApodTheme.dimensions.borderLarge)
                .background(ApodTheme.colors.primary, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(DesignSystemRes.drawable.ic_priority_high),
                contentDescription = null,
                modifier = Modifier.size(ApodTheme.dimensions.iconSmall),
                tint = ApodTheme.colors.onPrimary
            )
        }
    }
}

@LightDarkPreview
@Composable
private fun ErrorScreenPreview() = ApodColumnPreview(
    modifier = Modifier.padding(vertical = ApodTheme.dimensions.marginMedium)
) {
    ErrorScreen(message = stringResource(Res.string.picture_no_internet), onRetry = {})
}
