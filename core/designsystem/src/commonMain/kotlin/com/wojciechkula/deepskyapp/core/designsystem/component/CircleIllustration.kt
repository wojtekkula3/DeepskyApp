package com.wojciechkula.deepskyapp.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import com.wojciechkula.deepskyapp.core.designsystem.ApodBoxPreview
import com.wojciechkula.deepskyapp.core.designsystem.LightDarkPreview
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.ic_favourite_border
import com.wojciechkula.deepskyapp.core.designsystem.theme.ApodTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun CircleIllustration(
    icon: Painter,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(ApodTheme.dimensions.illustrationLarge)
            .background(ApodTheme.colors.primaryContainer, CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = icon,
            contentDescription = null,
            modifier = Modifier.size(ApodTheme.dimensions.iconExtraLarge),
            tint = ApodTheme.colors.primary
        )
    }
}

@LightDarkPreview
@Composable
private fun CircleIllustrationPreview() = ApodBoxPreview {
    CircleIllustration(icon = painterResource(DesignSystemRes.drawable.ic_favourite_border))
}
