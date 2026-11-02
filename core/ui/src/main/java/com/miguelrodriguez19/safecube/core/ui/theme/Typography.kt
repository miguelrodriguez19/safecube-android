package com.miguelrodriguez19.safecube.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.miguelrodriguez19.safecube.core.ui.R

val SafeCubeFontFamily: FontFamily = FontFamily(
    Font(R.font.manrope_regular, FontWeight.Normal),
    Font(R.font.manrope_medium, FontWeight.Medium),
    Font(R.font.manrope_semibold, FontWeight.SemiBold),
    Font(R.font.manrope_bold, FontWeight.Bold),
)

val SafeCubeFallbackFontFamily: FontFamily = FontFamily.SansSerif

private val DefaultTypography = Typography()

val SafeCubeTypography: Typography = DefaultTypography.copy(
    displayLarge = DefaultTypography.displayLarge.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Bold,
    ),
    displayMedium = DefaultTypography.displayMedium.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Bold,
    ),
    displaySmall = DefaultTypography.displaySmall.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Bold,
    ),
    headlineLarge = DefaultTypography.headlineLarge.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Bold,
    ),
    headlineMedium = DefaultTypography.headlineMedium.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.SemiBold,
    ),
    headlineSmall = DefaultTypography.headlineSmall.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.SemiBold,
    ),
    titleLarge = DefaultTypography.titleLarge.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.SemiBold,
    ),
    titleMedium = DefaultTypography.titleMedium.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.SemiBold,
    ),
    titleSmall = DefaultTypography.titleSmall.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    bodyLarge = DefaultTypography.bodyLarge.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Normal,
    ),
    bodyMedium = DefaultTypography.bodyMedium.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Normal,
    ),
    bodySmall = DefaultTypography.bodySmall.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Normal,
    ),
    labelLarge = DefaultTypography.labelLarge.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.SemiBold,
    ),
    labelMedium = DefaultTypography.labelMedium.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Medium,
    ),
    labelSmall = DefaultTypography.labelSmall.copy(
        fontFamily = SafeCubeFontFamily,
        fontWeight = FontWeight.Medium,
    ),
)
