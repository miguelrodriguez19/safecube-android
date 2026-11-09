package com.miguelrodriguez19.safecube.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.CloudOff
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.SyncProblem
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeElevation
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIconSize
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeShape
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing

enum class SafeCubeBannerVariant {
    Info,
    Warning,
    Error,
    Offline,
    Draft,
    Conflict,
    Success,
}

@Composable
fun SafeCubeBanner(
    message: String,
    variant: SafeCubeBannerVariant,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    actionEnabled: Boolean = true,
    actionLoading: Boolean = false,
) {
    requireOptionalAction(actionLabel, onAction)

    val stateLabel = stringResource(variant.labelRes())
    val palette = variant.palette()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                liveRegion = LiveRegionMode.Polite
                stateDescription = stateLabel
            },
        color = palette.container,
        contentColor = palette.content,
        shape = SafeCubeShape.Medium,
        tonalElevation = SafeCubeElevation.Level1,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SafeCubeSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
        ) {
            Row(
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Medium),
            ) {
                Icon(
                    imageVector = variant.icon(),
                    contentDescription = stateLabel,
                    modifier = Modifier.size(SafeCubeIconSize.Default),
                )
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.ExtraSmall),
                ) {
                    Text(
                        text = stateLabel,
                        style = MaterialTheme.typography.labelLarge,
                    )
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
            if (actionLabel != null && onAction != null) {
                SafeCubeButton(
                    text = actionLabel,
                    onClick = onAction,
                    variant = SafeCubeButtonVariant.Text,
                    enabled = actionEnabled,
                    loading = actionLoading,
                    modifier = Modifier
                        .align(Alignment.End)
                        .heightIn(min = SafeCubeSpacing.TouchTarget)
                )
            }
        }
    }
}

@Composable
fun SafeCubeConfirmationDialog(
    title: String,
    message: String,
    confirmLabel: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    destructive: Boolean = false,
    loading: Boolean = false,
    dismissLabel: String? = null,
) {
    require(confirmLabel.isNotBlank()) { "confirmLabel must not be blank" }
    require(dismissLabel == null || dismissLabel.isNotBlank()) {
        "dismissLabel must not be blank"
    }

    val resolvedDismissLabel = dismissLabel ?: stringResource(R.string.action_cancel)
    require(resolvedDismissLabel.isNotBlank()) {
        "dismissLabel must not be blank"
    }

    val dismissButton: @Composable () -> Unit = {
        SafeCubeButton(
            text = resolvedDismissLabel,
            onClick = onDismiss,
            variant = SafeCubeButtonVariant.Text,
            enabled = !loading,
            modifier = Modifier.heightIn(min = SafeCubeSpacing.TouchTarget),
        )
    }

    AlertDialog(
        onDismissRequest = { if (!loading) onDismiss() },
        modifier = modifier,
        title = {
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
            )
        },
        text = { Text(text = message) },
        confirmButton = {
            SafeCubeButton(
                text = confirmLabel,
                onClick = onConfirm,
                variant = if (destructive) {
                    SafeCubeButtonVariant.Destructive
                } else {
                    SafeCubeButtonVariant.Primary
                },
                enabled = !loading,
                loading = loading,
                modifier = Modifier
                    .heightIn(min = SafeCubeSpacing.TouchTarget)
            )
        },
        dismissButton = dismissButton,
    )
}

@Composable
fun SafeCubeProgress(
    message: String,
    modifier: Modifier = Modifier,
    progress: Float? = null,
) {
    require(progress == null || progress in 0f..1f) {
        "progress must be between 0 and 1"
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                liveRegion = LiveRegionMode.Polite
                stateDescription = message
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Medium),
    ) {
        if (progress == null) {
            CircularProgressIndicator()
        } else {
            CircularProgressIndicator(progress = { progress })
        }
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
fun SafeCubeEmptyState(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    requireOptionalAction(actionLabel, onAction)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(SafeCubeSpacing.Large),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
    ) {
        Text(
            text = title,
            modifier = Modifier.semantics { heading() },
            style = MaterialTheme.typography.headlineSmall,
        )
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
        )
        if (actionLabel != null && onAction != null) {
            SafeCubeButton(
                text = actionLabel,
                onClick = onAction,
                variant = SafeCubeButtonVariant.Primary,
                modifier = Modifier.heightIn(min = SafeCubeSpacing.TouchTarget),
            )
        }
    }
}

@Composable
fun SafeCubeRetryableError(
    title: String,
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
    retrying: Boolean = false,
) {
    val retryLabel = stringResource(R.string.design_system_feedback_retry)

    SafeCubeStateSurface(
        title = title,
        message = message,
        variant = SafeCubeBannerVariant.Error,
        modifier = modifier,
    ) {
        SafeCubeButton(
            text = retryLabel,
            onClick = onRetry,
            variant = SafeCubeButtonVariant.Primary,
            enabled = !retrying,
            loading = retrying,
            modifier = Modifier
                .align(Alignment.End)
                .heightIn(min = SafeCubeSpacing.TouchTarget)
        )
    }
}

@Composable
fun SafeCubeTerminalError(
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    requireOptionalAction(actionLabel, onAction)

    SafeCubeStateSurface(
        title = title,
        message = message,
        variant = SafeCubeBannerVariant.Error,
        modifier = modifier,
    ) {
        if (actionLabel != null && onAction != null) {
            SafeCubeButton(
                text = actionLabel,
                onClick = onAction,
                variant = SafeCubeButtonVariant.Primary,
                modifier = Modifier
                    .align(Alignment.End)
                    .heightIn(min = SafeCubeSpacing.TouchTarget),
            )
        }
    }
}

@Composable
private fun SafeCubeStateSurface(
    title: String,
    message: String,
    variant: SafeCubeBannerVariant,
    modifier: Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    val stateLabel = stringResource(variant.labelRes())
    val palette = variant.palette()

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                liveRegion = LiveRegionMode.Polite
                stateDescription = stateLabel
            },
        color = palette.container,
        contentColor = palette.content,
        shape = SafeCubeShape.Medium,
        tonalElevation = SafeCubeElevation.Level1,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SafeCubeSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Medium),
            ) {
                Icon(
                    imageVector = variant.icon(),
                    contentDescription = stateLabel,
                    modifier = Modifier.size(SafeCubeIconSize.Default),
                )
                Text(
                    text = stateLabel,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
            Text(
                text = title,
                modifier = Modifier.semantics { heading() },
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
            )
            content()
        }
    }
}

private fun requireOptionalAction(
    actionLabel: String?,
    onAction: (() -> Unit)?,
) {
    require((actionLabel == null) == (onAction == null)) {
        "actionLabel and onAction must be provided together"
    }
    require(actionLabel == null || actionLabel.isNotBlank()) {
        "actionLabel must not be blank"
    }
}

private data class SafeCubeFeedbackPalette(
    val container: Color,
    val content: Color,
)

private fun SafeCubeBannerVariant.labelRes(): Int = when (this) {
    SafeCubeBannerVariant.Info -> R.string.design_system_feedback_variant_info
    SafeCubeBannerVariant.Warning -> R.string.design_system_feedback_variant_warning
    SafeCubeBannerVariant.Error -> R.string.design_system_feedback_variant_error
    SafeCubeBannerVariant.Offline -> R.string.design_system_feedback_variant_offline
    SafeCubeBannerVariant.Draft -> R.string.design_system_feedback_variant_draft
    SafeCubeBannerVariant.Conflict -> R.string.design_system_feedback_variant_conflict
    SafeCubeBannerVariant.Success -> R.string.design_system_feedback_variant_success
}

private fun SafeCubeBannerVariant.icon(): ImageVector = when (this) {
    SafeCubeBannerVariant.Info -> Icons.Rounded.Info
    SafeCubeBannerVariant.Warning -> Icons.Rounded.Warning
    SafeCubeBannerVariant.Error -> Icons.Rounded.Error
    SafeCubeBannerVariant.Offline -> Icons.Rounded.CloudOff
    SafeCubeBannerVariant.Draft -> Icons.Rounded.Edit
    SafeCubeBannerVariant.Conflict -> Icons.Rounded.SyncProblem
    SafeCubeBannerVariant.Success -> Icons.Rounded.CheckCircle
}

@Composable
private fun SafeCubeBannerVariant.palette(): SafeCubeFeedbackPalette {
    val colors = MaterialTheme.colorScheme
    return when (this) {
        SafeCubeBannerVariant.Info -> SafeCubeFeedbackPalette(
            container = colors.primaryContainer,
            content = colors.onPrimaryContainer,
        )
        SafeCubeBannerVariant.Warning,
        SafeCubeBannerVariant.Draft,
        SafeCubeBannerVariant.Conflict -> SafeCubeFeedbackPalette(
            container = colors.tertiaryContainer,
            content = colors.onTertiaryContainer,
        )
        SafeCubeBannerVariant.Error -> SafeCubeFeedbackPalette(
            container = colors.errorContainer,
            content = colors.onErrorContainer,
        )
        SafeCubeBannerVariant.Offline -> SafeCubeFeedbackPalette(
            container = colors.surfaceVariant,
            content = colors.onSurfaceVariant,
        )
        SafeCubeBannerVariant.Success -> SafeCubeFeedbackPalette(
            container = colors.secondaryContainer,
            content = colors.onSecondaryContainer,
        )
    }
}
