package com.miguelrodriguez19.safecube.app.testsupport.visual

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.Shape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeElevation
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeMotion
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeShape
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeShapes
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import java.util.Locale

/**
 * Small, synthetic visual catalog for the SafeCube design-system infrastructure.
 *
 * The displayed hex values are derived from the active, non-dynamic Material color scheme so the
 * same preview proves that the approved light and dark palettes are actually applied.
 */
@Composable
fun VisualTokenCatalogSurface() {
    val colors = MaterialTheme.colorScheme

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = colors.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(SafeCubeSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Medium),
        ) {
            Text(
                text = "SafeCube token catalog",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Synthetic reference · no account or vault data",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
            Text(
                text = "Color roles",
                style = MaterialTheme.typography.titleMedium,
            )
            Column(verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
                ) {
                    ColorTokenCell("primary", colors.primary, colors.onPrimary)
                    ColorTokenCell("primaryContainer", colors.primaryContainer, colors.onPrimaryContainer)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
                ) {
                    ColorTokenCell("secondary", colors.secondary, colors.onSecondary)
                    ColorTokenCell("tertiary", colors.tertiary, colors.onTertiary)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
                ) {
                    ColorTokenCell("error", colors.error, colors.onError)
                    ColorTokenCell("surfaceVariant", colors.surfaceVariant, colors.onSurfaceVariant)
                }
            }
            Text(
                text = "Typography",
                style = MaterialTheme.typography.titleMedium,
            )
            Column(verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.ExtraSmall)) {
                Text("Heading · Manrope 600", style = MaterialTheme.typography.headlineSmall)
                Text("Body · Manrope 400", style = MaterialTheme.typography.bodyMedium)
                Text("Label · Manrope 500", style = MaterialTheme.typography.labelMedium)
            }
            Text(
                text = "Spacing · shapes · motion",
                style = MaterialTheme.typography.titleMedium,
            )
            SpacingTokenRow()
            ShapeTokenRow()
            Text(
                text = "Tokens: ${SafeCubeElevation.Level0.value.toInt()} · " +
                    "${SafeCubeElevation.Level1.value.toInt()} · " +
                    "${SafeCubeElevation.Level2.value.toInt()} · " +
                    "${SafeCubeElevation.Level3.value.toInt()} dp; " +
                    "${SafeCubeMotion.Instant.inWholeMilliseconds} · " +
                    "${SafeCubeMotion.Short.inWholeMilliseconds} · " +
                    "${SafeCubeMotion.Standard.inWholeMilliseconds} · " +
                    "${SafeCubeMotion.Long.inWholeMilliseconds} ms",
                color = colors.onSurfaceVariant,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun RowScope.ColorTokenCell(
    name: String,
    color: Color,
    contentColor: Color,
) {
    Surface(
        modifier = Modifier.weight(1f),
        color = color,
        contentColor = contentColor,
        shape = MaterialTheme.shapes.small,
    ) {
        Column(
            modifier = Modifier.padding(
                horizontal = SafeCubeSpacing.Small,
                vertical = SafeCubeSpacing.ExtraSmall,
            ),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.None),
        ) {
            Text(name, style = MaterialTheme.typography.labelLarge)
            Text(color.toHex(), style = MaterialTheme.typography.labelMedium)
        }
    }
}

@Composable
private fun SpacingTokenRow() {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
        verticalAlignment = Alignment.Bottom,
    ) {
        SpacingToken("4", SafeCubeSpacing.ExtraSmall, colors.primary)
        SpacingToken("8", SafeCubeSpacing.Small, colors.primary)
        SpacingToken("12", SafeCubeSpacing.Medium, colors.primary)
        SpacingToken("16", SafeCubeSpacing.Large, colors.primary)
        SpacingToken("24", SafeCubeSpacing.ExtraLarge, colors.primary)
        SpacingToken("32", SafeCubeSpacing.Huge, colors.primary)
        SpacingToken("48", SafeCubeSpacing.TouchTarget, colors.tertiary)
    }
}

@Composable
private fun SpacingToken(
    label: String,
    width: Dp,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .width(width)
                .height(8.dp)
                .background(color, MaterialTheme.shapes.extraSmall),
        )
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ShapeTokenRow() {
    val colors = MaterialTheme.colorScheme
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ShapeToken("4", SafeCubeShapes.extraSmall, colors.primaryContainer)
        ShapeToken("8", SafeCubeShapes.small, colors.primaryContainer)
        ShapeToken("12", SafeCubeShapes.medium, colors.secondaryContainer)
        ShapeToken("16", SafeCubeShapes.large, colors.tertiaryContainer)
        ShapeToken("full", SafeCubeShape.Full, colors.primary)
    }
}

@Composable
private fun ShapeToken(
    label: String,
    shape: Shape,
    color: Color,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(
            modifier = Modifier
                .size(40.dp)
                .clip(shape)
                .background(color),
        )
        Text(label, style = MaterialTheme.typography.labelSmall)
    }
}

private fun Color.toHex(): String = "#%06X".format(Locale.ROOT, toArgb() and 0xFFFFFF)
