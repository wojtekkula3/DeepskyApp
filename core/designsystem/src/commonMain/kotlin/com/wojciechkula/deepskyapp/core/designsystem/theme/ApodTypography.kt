package com.wojciechkula.deepskyapp.core.designsystem.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.wojciechkula.deepskyapp.core.designsystem.resources.DesignSystemRes
import com.wojciechkula.deepskyapp.core.designsystem.resources.inter_bold
import com.wojciechkula.deepskyapp.core.designsystem.resources.inter_medium
import com.wojciechkula.deepskyapp.core.designsystem.resources.inter_regular
import com.wojciechkula.deepskyapp.core.designsystem.resources.inter_semibold
import org.jetbrains.compose.resources.Font

@Composable
internal fun interFontFamily(): FontFamily = FontFamily(
    Font(DesignSystemRes.font.inter_regular, FontWeight.Normal),
    Font(DesignSystemRes.font.inter_medium, FontWeight.Medium),
    Font(DesignSystemRes.font.inter_semibold, FontWeight.SemiBold),
    Font(DesignSystemRes.font.inter_bold, FontWeight.Bold)
)

@Suppress("MagicNumber")
@Composable
internal fun apodTypography(): Typography {
    val inter = interFontFamily()
    val baseline = Typography()
    return baseline.copy(
        displayLarge = baseline.displayLarge.copy(fontFamily = inter),
        displayMedium = baseline.displayMedium.copy(fontFamily = inter),
        displaySmall = baseline.displaySmall.copy(fontFamily = inter),
        headlineLarge = baseline.headlineLarge.copy(fontFamily = inter),
        headlineMedium = interStyle(inter, FontWeight.SemiBold, size = 24.sp, lineHeight = 30.sp),
        headlineSmall = baseline.headlineSmall.copy(fontFamily = inter),
        titleLarge = interStyle(inter, FontWeight.SemiBold, size = 22.sp, lineHeight = 28.sp),
        titleMedium = interStyle(inter, FontWeight.SemiBold, size = 18.sp, lineHeight = 24.sp),
        titleSmall = interStyle(inter, FontWeight.SemiBold, size = 16.sp, lineHeight = 22.sp),
        bodyLarge = interStyle(inter, FontWeight.Normal, size = 16.sp, lineHeight = 24.sp),
        bodyMedium = interStyle(inter, FontWeight.Normal, size = 14.sp, lineHeight = 20.sp),
        bodySmall = interStyle(inter, FontWeight.Normal, size = 12.sp, lineHeight = 16.sp),
        labelLarge = interStyle(inter, FontWeight.Medium, size = 14.sp, lineHeight = 20.sp),
        labelMedium = interStyle(inter, FontWeight.Medium, size = 12.sp, lineHeight = 16.sp),
        labelSmall = baseline.labelSmall.copy(fontFamily = inter)
    )
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
