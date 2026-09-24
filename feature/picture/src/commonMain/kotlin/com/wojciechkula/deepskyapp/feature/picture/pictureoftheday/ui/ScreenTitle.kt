package com.wojciechkula.deepskyapp.feature.picture.pictureoftheday.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_of_the_day_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ScreenTitle() {
    Text(
        text = stringResource(Res.string.picture_of_the_day_title),
        modifier = Modifier.padding(
            horizontal = ApodTheme.dimensions.marginLarge,
            vertical = ApodTheme.dimensions.marginMedium
        ),
        style = ApodTheme.typography.headlineMedium,
        color = ApodTheme.colors.onSurface
    )
}

@LightDarkPreview
@Composable
private fun ScreenTitlePreview() = ApodColumnPreview {
    ScreenTitle()
}
