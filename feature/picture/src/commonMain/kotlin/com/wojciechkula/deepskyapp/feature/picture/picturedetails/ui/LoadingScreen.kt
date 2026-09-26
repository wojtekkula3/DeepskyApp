package com.wojciechkula.deepskyapp.feature.picture.picturedetails.ui

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
import com.wojciechkula.deepskyapp.core.designsystem.component.BackButton
import com.wojciechkula.deepskyapp.core.designsystem.component.ScreenTitleBar
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_title
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LoadingScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ScreenTitleBar(
            title = stringResource(Res.string.picture_details_title),
            navigationIcon = { BackButton(onClick = onBackClick) }
        )
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
    LoadingScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
}
