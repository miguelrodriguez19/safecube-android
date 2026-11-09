package com.miguelrodriguez19.safecube.core.ui.theme

import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class SafeCubeTokensTest {
    @Test
    fun lightPaletteUsesTheApprovedBrandRoles() {
        assertEquals(Color(0xFF006A6A), SafeCubeLightColorScheme.primary)
        assertEquals(Color.White, SafeCubeLightColorScheme.onPrimary)
        assertEquals(Color(0xFF9CF1EF), SafeCubeLightColorScheme.primaryContainer)
        assertEquals(Color(0xFF002020), SafeCubeLightColorScheme.onPrimaryContainer)
        assertEquals(Color(0xFF4A6363), SafeCubeLightColorScheme.secondary)
        assertEquals(Color.White, SafeCubeLightColorScheme.onSecondary)
        assertEquals(Color(0xFFCCE8E7), SafeCubeLightColorScheme.secondaryContainer)
        assertEquals(Color(0xFF051F1F), SafeCubeLightColorScheme.onSecondaryContainer)
        assertEquals(Color(0xFF8B4A20), SafeCubeLightColorScheme.tertiary)
        assertEquals(Color.White, SafeCubeLightColorScheme.onTertiary)
        assertEquals(Color(0xFFFFDCC7), SafeCubeLightColorScheme.tertiaryContainer)
        assertEquals(Color(0xFF321200), SafeCubeLightColorScheme.onTertiaryContainer)
        assertEquals(Color(0xFFBA1A1A), SafeCubeLightColorScheme.error)
        assertEquals(Color.White, SafeCubeLightColorScheme.onError)
        assertEquals(Color(0xFFF5FAF9), SafeCubeLightColorScheme.background)
        assertEquals(Color(0xFF171D1D), SafeCubeLightColorScheme.onBackground)
        assertEquals(Color(0xFFF5FAF9), SafeCubeLightColorScheme.surface)
        assertEquals(Color(0xFF171D1D), SafeCubeLightColorScheme.onSurface)
        assertEquals(Color(0xFFDAE5E4), SafeCubeLightColorScheme.surfaceVariant)
        assertEquals(Color(0xFF3F4948), SafeCubeLightColorScheme.onSurfaceVariant)
        assertEquals(Color(0xFF6F7978), SafeCubeLightColorScheme.outline)
    }

    @Test
    fun darkPaletteUsesTheApprovedBrandRoles() {
        assertEquals(Color(0xFF80D5D3), SafeCubeDarkColorScheme.primary)
        assertEquals(Color(0xFF003737), SafeCubeDarkColorScheme.onPrimary)
        assertEquals(Color(0xFF00504F), SafeCubeDarkColorScheme.primaryContainer)
        assertEquals(Color(0xFF9CF1EF), SafeCubeDarkColorScheme.onPrimaryContainer)
        assertEquals(Color(0xFFB1CCCC), SafeCubeDarkColorScheme.secondary)
        assertEquals(Color(0xFF1C3535), SafeCubeDarkColorScheme.onSecondary)
        assertEquals(Color(0xFF334B4B), SafeCubeDarkColorScheme.secondaryContainer)
        assertEquals(Color(0xFFCCE8E7), SafeCubeDarkColorScheme.onSecondaryContainer)
        assertEquals(Color(0xFFFFB68C), SafeCubeDarkColorScheme.tertiary)
        assertEquals(Color(0xFF512400), SafeCubeDarkColorScheme.onTertiary)
        assertEquals(Color(0xFF6D330A), SafeCubeDarkColorScheme.tertiaryContainer)
        assertEquals(Color(0xFFFFDCC7), SafeCubeDarkColorScheme.onTertiaryContainer)
        assertEquals(Color(0xFFFFB4AB), SafeCubeDarkColorScheme.error)
        assertEquals(Color(0xFF690005), SafeCubeDarkColorScheme.onError)
        assertEquals(Color(0xFF0E1515), SafeCubeDarkColorScheme.background)
        assertEquals(Color(0xFFDEE4E3), SafeCubeDarkColorScheme.onBackground)
        assertEquals(Color(0xFF0E1515), SafeCubeDarkColorScheme.surface)
        assertEquals(Color(0xFFDEE4E3), SafeCubeDarkColorScheme.onSurface)
        assertEquals(Color(0xFF3F4948), SafeCubeDarkColorScheme.surfaceVariant)
        assertEquals(Color(0xFFBEC9C8), SafeCubeDarkColorScheme.onSurfaceVariant)
        assertEquals(Color(0xFF899392), SafeCubeDarkColorScheme.outline)
    }

    @Test
    fun spacingAndTouchTargetExposeTheApprovedScale() {
        assertEquals(0.dp, SafeCubeSpacing.None)
        assertEquals(4.dp, SafeCubeSpacing.ExtraSmall)
        assertEquals(8.dp, SafeCubeSpacing.Small)
        assertEquals(12.dp, SafeCubeSpacing.Medium)
        assertEquals(16.dp, SafeCubeSpacing.Large)
        assertEquals(24.dp, SafeCubeSpacing.ExtraLarge)
        assertEquals(32.dp, SafeCubeSpacing.Huge)
        assertEquals(48.dp, SafeCubeSpacing.TouchTarget)
    }

    @Test
    fun elevationAndIconSizesExposeTheApprovedScale() {
        assertEquals(0.dp, SafeCubeElevation.Level0)
        assertEquals(1.dp, SafeCubeElevation.Level1)
        assertEquals(3.dp, SafeCubeElevation.Level2)
        assertEquals(6.dp, SafeCubeElevation.Level3)
        assertEquals(16.dp, SafeCubeIconSize.Small)
        assertEquals(20.dp, SafeCubeIconSize.Medium)
        assertEquals(24.dp, SafeCubeIconSize.Default)
        assertEquals(32.dp, SafeCubeIconSize.Large)
    }

    @Test
    fun motionExposesTheApprovedDurations() {
        assertEquals(0, SafeCubeMotion.Instant.inWholeMilliseconds)
        assertEquals(150, SafeCubeMotion.Short.inWholeMilliseconds)
        assertEquals(250, SafeCubeMotion.Standard.inWholeMilliseconds)
        assertEquals(350, SafeCubeMotion.Long.inWholeMilliseconds)
    }

    @Test
    fun motionAliasesUseTheApprovedDurations() {
        assertEquals(SafeCubeMotion.Standard, SafeCubeMotion.Enter)
        assertEquals(SafeCubeMotion.Short, SafeCubeMotion.Exit)
        assertEquals(SafeCubeMotion.Short, SafeCubeMotion.StateChange)
        assertEquals(SafeCubeMotion.Short, SafeCubeMotion.Feedback)
    }

    @Test
    fun shapesExposeTheApprovedScaleIncludingFull() {
        assertEquals(SafeCubeShape.ExtraSmall, SafeCubeShapes.extraSmall)
        assertEquals(SafeCubeShape.Small, SafeCubeShapes.small)
        assertEquals(SafeCubeShape.Medium, SafeCubeShapes.medium)
        assertEquals(SafeCubeShape.Large, SafeCubeShapes.large)
        assertEquals(CircleShape, SafeCubeShape.Full)
    }

    @Test
    fun typographyUsesManropeForAllMaterialRoles() {
        assertEquals(FontFamily.SansSerif, SafeCubeFallbackFontFamily)
        val styles = listOf(
            SafeCubeTypography.displayLarge,
            SafeCubeTypography.displayMedium,
            SafeCubeTypography.displaySmall,
            SafeCubeTypography.headlineLarge,
            SafeCubeTypography.headlineMedium,
            SafeCubeTypography.headlineSmall,
            SafeCubeTypography.titleLarge,
            SafeCubeTypography.titleMedium,
            SafeCubeTypography.titleSmall,
            SafeCubeTypography.bodyLarge,
            SafeCubeTypography.bodyMedium,
            SafeCubeTypography.bodySmall,
            SafeCubeTypography.labelLarge,
            SafeCubeTypography.labelMedium,
            SafeCubeTypography.labelSmall,
        )

        styles.forEach { style ->
            assertEquals(SafeCubeFontFamily, style.fontFamily)
        }
    }
}
