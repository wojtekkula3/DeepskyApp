package com.wojciechkula.deepskyapp.feature.about

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.wojciechkula.deepskyapp.core.designsystem.ApodScreenPreview
import com.wojciechkula.deepskyapp.core.designsystem.ScreenLightDarkPreview
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
    Scaffold { padding ->
        val layoutDirection = LocalLayoutDirection.current
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    start = padding.calculateStartPadding(layoutDirection),
                    top = padding.calculateTopPadding(),
                    end = padding.calculateEndPadding(layoutDirection)
                )
                .verticalScroll(rememberScrollState())
                .padding(bottom = ApodTheme.dimensions.margin2xLarge)
        ) {
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
