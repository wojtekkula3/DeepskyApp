package com.wojciechkula.deepskyapp.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.inter_medium
import com.wojciechkula.deepskyapp.core.designsystem.resources.inter_regular
import com.wojciechkula.deepskyapp.core.designsystem.resources.inter_semibold
import org.jetbrains.compose.resources.Font

@Immutable
data class ApodTypography(
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyLargeBold: TextStyle,
    val bodyMedium: TextStyle,
    val bodyMediumBold: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle
)

@Composable
private fun interFontFamily(): FontFamily = FontFamily(
    Font(DesignSystemRes.font.inter_regular, FontWeight.Normal),
    Font(DesignSystemRes.font.inter_medium, FontWeight.Medium),
    Font(DesignSystemRes.font.inter_semibold, FontWeight.SemiBold)
)

@Suppress("MagicNumber")
@Composable
internal fun apodTypography(): ApodTypography {
    val inter = interFontFamily()
    return remember(inter) {
        ApodTypography(
            headlineLarge = interStyle(inter, FontWeight.SemiBold, size = 32.sp, lineHeight = 40.sp),
            headlineMedium = interStyle(inter, FontWeight.SemiBold, size = 24.sp, lineHeight = 30.sp),
            titleLarge = interStyle(inter, FontWeight.SemiBold, size = 22.sp, lineHeight = 28.sp),
            titleMedium = interStyle(inter, FontWeight.SemiBold, size = 18.sp, lineHeight = 24.sp),
            titleSmall = interStyle(inter, FontWeight.SemiBold, size = 16.sp, lineHeight = 22.sp),
            bodyLarge = interStyle(inter, FontWeight.Normal, size = 16.sp, lineHeight = 24.sp),
            bodyLargeBold = interStyle(inter, FontWeight.SemiBold, size = 16.sp, lineHeight = 24.sp),
            bodyMedium = interStyle(inter, FontWeight.Normal, size = 14.sp, lineHeight = 20.sp),
            bodyMediumBold = interStyle(inter, FontWeight.SemiBold, size = 14.sp, lineHeight = 20.sp),
            bodySmall = interStyle(inter, FontWeight.Normal, size = 12.sp, lineHeight = 16.sp),
            labelLarge = interStyle(inter, FontWeight.Medium, size = 14.sp, lineHeight = 20.sp),
            labelMedium = interStyle(inter, FontWeight.Medium, size = 12.sp, lineHeight = 16.sp)
        )
    }
}

// Material components read these slots internally, so every slot carries Inter — an unset one falls back to the system font.
internal fun ApodTypography.toMaterialTypography(): Typography {
    val baseline = Typography()
    val family = bodyLarge.fontFamily
    return Typography(
        displayLarge = baseline.displayLarge.copy(fontFamily = family),
        displayMedium = baseline.displayMedium.copy(fontFamily = family),
        displaySmall = baseline.displaySmall.copy(fontFamily = family),
        headlineLarge = headlineLarge,
        headlineMedium = headlineMedium,
        headlineSmall = headlineMedium,
        titleLarge = titleLarge,
        titleMedium = titleMedium,
        titleSmall = titleSmall,
        bodyLarge = bodyLarge,
        bodyMedium = bodyMedium,
        bodySmall = bodySmall,
        labelLarge = labelLarge,
        labelMedium = labelMedium,
        labelSmall = baseline.labelSmall.copy(fontFamily = family)
    )
}

internal val LocalApodTypography = staticCompositionLocalOf<ApodTypography> {
    error("ApodTypography is provided by ApodTheme")
}

private fun interStyle(
    family: FontFamily,
    weight: FontWeight,
    size: TextUnit,
    lineHeight: TextUnit
): TextStyle = TextStyle(
    fontFamily = family,
    fontWeight = weight,
    fontSize = size,
    lineHeight = lineHeight
)
