package com.wojciechkula.deepskyapp.feature.picture.picturedetails.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.wojciechkula.deepskyapp.core.designsystem.ApodColumnPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.component.BackButton
import com.wojciechkula.deepskyapp.core.designsystem.component.CircleIllustration
import com.wojciechkula.deepskyapp.core.designsystem.component.ContentCard
import com.wojciechkula.deepskyapp.core.designsystem.component.ScreenTitleBar
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite_border
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import com.wojciechkula.deepskyapp.feature.picture.resources.Res
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_not_found
import com.wojciechkula.deepskyapp.feature.picture.resources.picture_details_title
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun NotFoundScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        ScreenTitleBar(
            title = stringResource(Res.string.picture_details_title),
            navigationIcon = { BackButton(onClick = onBackClick) }
        )
        ContentCard(modifier = Modifier.padding(top = ApodTheme.dimensions.marginLarge)) {
            Column(
                modifier = Modifier.padding(ApodTheme.dimensions.marginLarge),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircleIllustration(icon = painterResource(DesignSystemRes.drawable.ic_favourite_border))
                Text(
                    text = stringResource(Res.string.picture_details_not_found),
                    modifier = Modifier.padding(top = ApodTheme.dimensions.marginMedium),
                    style = ApodTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = ApodTheme.colors.onSurface
                )
            }
        }
    }
}

@LightDarkPreview
@Composable
private fun NotFoundScreenPreview() = ApodColumnPreview {
    NotFoundScreen(onBackClick = {}, modifier = Modifier.fillMaxSize())
}
