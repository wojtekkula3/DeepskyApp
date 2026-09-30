package com.wojciechkula.deepskyapp.feature.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.wojciechkula.deepskyapp.core.designsystem.ApodScreenPreview
import com.wojciechkula.deepskyapp.core.designsystem.ScreenLightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.ApodScaffold
import com.wojciechkula.deepskyapp.core.designsystem.component.BackButton
import com.wojciechkula.deepskyapp.core.designsystem.component.ScreenTitleBar
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.feature.about.resources.Res
import com.wojciechkula.deepskyapp.feature.about.resources.about_screen_title
import com.wojciechkula.deepskyapp.feature.about.ui.DescriptionCard
import com.wojciechkula.deepskyapp.feature.about.ui.HeaderCard
import org.jetbrains.compose.resources.stringResource

@Composable
fun About(onBack: () -> Unit) {
    AboutScreen(onBack = onBack)
}

@Composable
private fun AboutScreen(onBack: () -> Unit) {
    ApodScaffold(contentEndPadding = ApodTheme.dimensions.margin2xLarge) {
        Column(modifier = scrollableModifier) {
            ScreenTitleBar(
                title = stringResource(Res.string.about_screen_title),
                navigationIcon = { BackButton(onClick = onBack) }
            )
            Spacer(modifier = Modifier.height(ApodTheme.dimensions.marginSmall))
            HeaderCard()
            DescriptionCard(modifier = Modifier.padding(top = ApodTheme.dimensions.marginMedium))
        }
    }
}

@ScreenLightDarkPreview
@Composable
private fun AboutPreview() = ApodScreenPreview {
    AboutScreen(onBack = {})
}
