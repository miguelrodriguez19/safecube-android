package com.miguelrodriguez19.safecube.core.ui.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIconSize
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing

enum class SafeCubeButtonVariant {
    Primary,
    Secondary,
    Text,
    Destructive,
}

@Composable
fun SafeCubeButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    variant: SafeCubeButtonVariant = SafeCubeButtonVariant.Primary,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    require(text.isNotBlank()) { "Button text must not be blank" }
    val loadingLabel = stringResource(R.string.design_system_loading)
    val effectiveEnabled = enabled && !loading
    val buttonModifier = modifier
        .defaultMinSize(minHeight = SafeCubeSpacing.TouchTarget)
        .semantics {
            if (loading) {
                stateDescription = loadingLabel
            }
        }
    val content: @Composable RowScope.() -> Unit = {
        SafeCubeButtonContent(text = text, loading = loading)
    }

    when (variant) {
        SafeCubeButtonVariant.Primary -> Button(
            onClick = { if (effectiveEnabled) onClick() },
            modifier = buttonModifier,
            enabled = effectiveEnabled,
            interactionSource = interactionSource,
            content = content,
        )

        SafeCubeButtonVariant.Secondary -> OutlinedButton(
            onClick = { if (effectiveEnabled) onClick() },
            modifier = buttonModifier,
            enabled = effectiveEnabled,
            interactionSource = interactionSource,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.secondary,
            ),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
            content = content,
        )

        SafeCubeButtonVariant.Text -> TextButton(
            onClick = { if (effectiveEnabled) onClick() },
            modifier = buttonModifier,
            enabled = effectiveEnabled,
            interactionSource = interactionSource,
            colors = ButtonDefaults.textButtonColors(
                contentColor = LocalContentColor.current,
            ),
            content = content,
        )

        SafeCubeButtonVariant.Destructive -> Button(
            onClick = { if (effectiveEnabled) onClick() },
            modifier = buttonModifier,
            enabled = effectiveEnabled,
            interactionSource = interactionSource,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            ),
            content = content,
        )
    }
}

@Composable
fun SafeCubeIconButton(
    imageVector: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    loading: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    require(contentDescription.isNotBlank()) { "Icon button description must not be blank" }
    val loadingLabel = stringResource(R.string.design_system_loading)
    val effectiveEnabled = enabled && !loading

    IconButton(
        onClick = { if (effectiveEnabled) onClick() },
        modifier = modifier
            .defaultMinSize(
                minWidth = SafeCubeSpacing.TouchTarget,
                minHeight = SafeCubeSpacing.TouchTarget,
            )
            .semantics {
                if (loading) {
                    stateDescription = loadingLabel
                }
            },
        enabled = effectiveEnabled,
        interactionSource = interactionSource,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = imageVector,
                contentDescription = contentDescription,
                modifier = Modifier.alpha(if (loading) 0.6f else 1f),
            )
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(SafeCubeIconSize.Medium),
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}

@Composable
private fun SafeCubeButtonContent(
    text: String,
    loading: Boolean,
) {
    Box(
        modifier = Modifier.defaultMinSize(minHeight = SafeCubeSpacing.TouchTarget),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
        ) {
            Text(text = text)
            if (loading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(SafeCubeIconSize.Medium),
                    strokeWidth = 2.dp,
                )
            }
        }
    }
}
