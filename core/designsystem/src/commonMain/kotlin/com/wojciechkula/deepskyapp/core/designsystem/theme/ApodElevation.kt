package com.wojciechkula.deepskyapp.core.designsystem.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class ApodElevation(
    val detailsCard: Dp,
    val galleryCard: Dp,
    val floatingNavigation: Dp,
    val iconButton: Dp
)

internal val ApodLightElevation = ApodElevation(
    detailsCard = 6.dp,
    galleryCard = 4.dp,
    floatingNavigation = 8.dp,
    iconButton = 6.dp
)

internal val ApodDarkElevation = ApodElevation(
    detailsCard = 4.dp,
    galleryCard = 3.dp,
    floatingNavigation = 6.dp,
    iconButton = 5.dp
)

internal val LocalApodElevation = staticCompositionLocalOf { ApodLightElevation }
