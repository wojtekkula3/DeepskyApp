package com.wojciechkula.deepskyapp.feature.favourites.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview

@Composable
internal fun LoadingScreen(
    onAboutClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ScreenTitle(onAboutClick = onAboutClick)
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@LightDarkPreview
@Composable
private fun LoadingScreenPreview() = ApodColumnPreview {
    LoadingScreen(onAboutClick = {}, modifier = Modifier.fillMaxSize())
}
