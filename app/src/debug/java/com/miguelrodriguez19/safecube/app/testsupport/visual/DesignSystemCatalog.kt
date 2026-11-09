package com.miguelrodriguez19.safecube.app.testsupport.visual

import android.content.res.Configuration
import androidx.compose.foundation.interaction.FocusInteraction
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.miguelrodriguez19.safecube.R
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBanner
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBannerVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBottomNavigation
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButtonVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeCard
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeConfirmationDialog
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeEmptyState
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeIconButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeItemRow
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeNavigationItem
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeProgress
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeRetryableError
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTerminalError
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTextField
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTopAppBar
import com.miguelrodriguez19.safecube.core.ui.component.SecretOutlinedTextField
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIcons
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Debug-only, synthetic catalog for the public SafeCube component families.
 *
 * The catalog deliberately contains no account, vault, credential, token or recovery-key data.
 * Each page is bounded so a screenshot never relies on a partially visible scrolling surface.
 */
@Composable
fun DesignSystemCatalogActionsSurface() {
    val pressedButtonInteractionSource = rememberPressedInteractionSource()
    val pressedIconInteractionSource = rememberPressedInteractionSource()

    CatalogPage(
        title = stringResource(R.string.design_catalog_actions_title),
        description = stringResource(R.string.design_catalog_actions_description),
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small)) {
            SafeCubeButton(
                text = stringResource(R.string.design_catalog_primary),
                onClick = {},
                modifier = Modifier.weight(1f),
                variant = SafeCubeButtonVariant.Primary,
            )
            SafeCubeButton(
                text = stringResource(R.string.design_catalog_secondary),
                onClick = {},
                modifier = Modifier.weight(1f),
                variant = SafeCubeButtonVariant.Secondary,
                interactionSource = pressedButtonInteractionSource,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small)) {
            SafeCubeButton(
                text = stringResource(R.string.design_catalog_text_action),
                onClick = {},
                modifier = Modifier.weight(1f),
                variant = SafeCubeButtonVariant.Text,
            )
            SafeCubeButton(
                text = stringResource(R.string.design_catalog_destructive),
                onClick = {},
                modifier = Modifier.weight(1f),
                variant = SafeCubeButtonVariant.Destructive,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small)) {
            SafeCubeButton(
                text = stringResource(R.string.design_catalog_loading),
                onClick = {},
                modifier = Modifier.weight(1f),
                variant = SafeCubeButtonVariant.Primary,
                loading = true,
            )
            SafeCubeButton(
                text = stringResource(R.string.design_catalog_disabled),
                onClick = {},
                modifier = Modifier.weight(1f),
                variant = SafeCubeButtonVariant.Secondary,
                enabled = false,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small)) {
            SafeCubeIconButton(
                imageVector = SafeCubeIcons.Settings,
                contentDescription = stringResource(R.string.design_catalog_settings_description),
                onClick = {},
                modifier = Modifier.size(SafeCubeSpacing.TouchTarget),
                interactionSource = pressedIconInteractionSource,
            )
            SafeCubeIconButton(
                imageVector = SafeCubeIcons.Sync,
                contentDescription = stringResource(R.string.design_catalog_sync_description),
                onClick = {},
                modifier = Modifier.size(SafeCubeSpacing.TouchTarget),
                enabled = false,
            )
        }
    }
}

@Composable
fun DesignSystemCatalogFieldsSurface() {
    val focusedInteractionSource = rememberFocusedInteractionSource()

    CatalogPage(
        title = stringResource(R.string.design_catalog_form_title),
        description = stringResource(R.string.design_catalog_form_description),
    ) {
        SafeCubeTextField(
            value = stringResource(R.string.design_catalog_value),
            onValueChange = {},
            label = stringResource(R.string.design_catalog_label),
            modifier = Modifier.fillMaxWidth(),
            interactionSource = focusedInteractionSource,
        )
        SafeCubeTextField(
            value = stringResource(R.string.design_catalog_error_value),
            onValueChange = {},
            label = stringResource(R.string.design_catalog_label),
            modifier = Modifier.fillMaxWidth(),
            isError = true,
            supportingText = stringResource(R.string.design_catalog_error_supporting),
        )
        SecretOutlinedTextField(
            value = stringResource(R.string.design_catalog_mask_sample),
            onValueChange = {},
            label = { Text(stringResource(R.string.design_catalog_secret_label)) },
            modifier = Modifier.fillMaxWidth(),
            supportingText = {
                Text(stringResource(R.string.design_catalog_secret_supporting))
            },
            allowReveal = true,
        )
        SafeCubeTextField(
            value = stringResource(R.string.design_catalog_disabled_field_value),
            onValueChange = {},
            label = stringResource(R.string.design_catalog_disabled_field_label),
            modifier = Modifier.fillMaxWidth(),
            enabled = false,
        )
    }
}

@Composable
fun DesignSystemCatalogFieldsFontScale200Surface() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SafeCubeSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
        ) {
            Text(
                text = stringResource(R.string.design_catalog_form_title),
                style = MaterialTheme.typography.headlineSmall,
            )
            SafeCubeTextField(
                value = stringResource(R.string.design_catalog_error_value),
                onValueChange = {},
                label = stringResource(R.string.design_catalog_adaptive_long_label),
                modifier = Modifier.fillMaxWidth(),
                isError = true,
                supportingText = stringResource(R.string.design_catalog_error_supporting),
            )
            SecretOutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(stringResource(R.string.design_catalog_secret_label)) },
                modifier = Modifier.fillMaxWidth(),
                supportingText = {
                    Text(stringResource(R.string.design_catalog_secret_supporting))
                },
                allowReveal = true,
            )
            SafeCubeButton(
                text = stringResource(R.string.design_catalog_adaptive_action),
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = SafeCubeButtonVariant.Primary,
            )
        }
    }
}

@Composable
fun DesignSystemCatalogNavigationSurface() {
    CatalogPage(
        title = stringResource(R.string.design_catalog_navigation_title),
        description = stringResource(R.string.design_catalog_navigation_description),
    ) {
        SafeCubeTopAppBar(
            title = stringResource(R.string.design_catalog_navigation_title),
            navigationIcon = SafeCubeIcons.Back,
            navigationContentDescription = stringResource(R.string.design_catalog_back),
            onNavigationClick = {},
        )
        SafeCubeCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = stringResource(R.string.design_catalog_card_title),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = stringResource(R.string.design_catalog_card_message),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        SafeCubeItemRow(
            title = stringResource(R.string.design_catalog_item_title),
            supportingText = stringResource(R.string.design_catalog_item_supporting),
            icon = SafeCubeIcons.Sync,
            stateDescription = stringResource(R.string.design_catalog_item_state),
            onClick = {},
            enabled = true,
        )
        SafeCubeBottomNavigation(
            items = listOf(
                SafeCubeNavigationItem(
                    id = "vault",
                    label = stringResource(R.string.design_catalog_vault),
                    icon = SafeCubeIcons.Vault,
                    enabled = true,
                ),
                SafeCubeNavigationItem(
                    id = "settings",
                    label = stringResource(R.string.design_catalog_settings_tab),
                    icon = SafeCubeIcons.Settings,
                    enabled = false,
                ),
            ),
            selectedItemId = "vault",
            onItemClick = {},
        )
    }
}

@Composable
fun DesignSystemCatalogFeedbackStatusSurface() {
    CatalogPage(
        title = stringResource(R.string.design_catalog_feedback_title),
        description = stringResource(R.string.design_catalog_feedback_description),
    ) {
        SafeCubeBanner(
            message = stringResource(R.string.design_catalog_info),
            variant = SafeCubeBannerVariant.Info,
        )
        SafeCubeBanner(
            message = stringResource(R.string.design_catalog_warning),
            variant = SafeCubeBannerVariant.Warning,
            actionLabel = stringResource(R.string.design_catalog_review),
            onAction = {},
        )
        SafeCubeBanner(
            message = stringResource(R.string.design_catalog_error),
            variant = SafeCubeBannerVariant.Error,
            actionLabel = stringResource(R.string.design_catalog_retry),
            onAction = {},
        )
    }
}

@Composable
fun DesignSystemCatalogFeedbackResolutionSurface() {
    CatalogPage(
        title = stringResource(R.string.design_catalog_feedback_title),
        description = stringResource(R.string.design_catalog_feedback_description),
    ) {
        SafeCubeBanner(
            message = stringResource(R.string.design_catalog_draft),
            variant = SafeCubeBannerVariant.Draft,
            actionLabel = stringResource(R.string.design_catalog_publish),
            onAction = {},
        )
        SafeCubeBanner(
            message = stringResource(R.string.design_catalog_conflict),
            variant = SafeCubeBannerVariant.Conflict,
            actionLabel = stringResource(R.string.design_catalog_review),
            onAction = {},
        )
        SafeCubeBanner(
            message = stringResource(R.string.design_catalog_success),
            variant = SafeCubeBannerVariant.Success,
        )
    }
}

@Composable
fun DesignSystemCatalogFeedbackAvailabilitySurface() {
    CatalogPage(
        title = stringResource(R.string.design_catalog_feedback_title),
        description = stringResource(R.string.design_catalog_feedback_description),
    ) {
        SafeCubeBanner(
            message = stringResource(R.string.design_catalog_offline),
            variant = SafeCubeBannerVariant.Offline,
        )
        SafeCubeProgress(
            message = stringResource(R.string.design_catalog_progress),
            progress = 0.65f,
        )
        SafeCubeEmptyState(
            title = stringResource(R.string.design_catalog_empty_title),
            message = stringResource(R.string.design_catalog_empty_message),
            actionLabel = stringResource(R.string.design_catalog_create),
            onAction = {},
        )
    }
}

@Composable
fun DesignSystemCatalogFeedbackRecoverySurface() {
    CatalogPage(
        title = stringResource(R.string.design_catalog_feedback_title),
        description = stringResource(R.string.design_catalog_feedback_description),
    ) {
        SafeCubeRetryableError(
            title = stringResource(R.string.design_catalog_retryable_title),
            message = stringResource(R.string.design_catalog_retryable_message),
            onRetry = {},
        )
        SafeCubeTerminalError(
            title = stringResource(R.string.design_catalog_terminal_title),
            message = stringResource(R.string.design_catalog_terminal_message),
            actionLabel = stringResource(R.string.design_catalog_review),
            onAction = {},
        )
    }
}

@Composable
fun DesignSystemCatalogConfirmationSurface(loading: Boolean = false) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        SafeCubeConfirmationDialog(
            title = stringResource(R.string.design_catalog_confirmation_title),
            message = stringResource(R.string.design_catalog_confirmation_message),
            confirmLabel = stringResource(R.string.design_catalog_confirm),
            onConfirm = {},
            onDismiss = {},
            destructive = true,
            loading = loading,
            dismissLabel = stringResource(R.string.design_catalog_cancel),
        )
    }
}

@Composable
private fun CatalogPage(
    title: String,
    description: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SafeCubeSpacing.Large),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
            content = {
                Text(text = title, style = MaterialTheme.typography.headlineSmall)
                Text(
                    text = description,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall,
                )
                content()
            },
        )
    }
}

@Composable
private fun rememberPressedInteractionSource(): MutableInteractionSource {
    return remember { CatalogInteractionSource(PressInteraction.Press(Offset.Zero)) }
}

@Composable
private fun rememberFocusedInteractionSource(): MutableInteractionSource {
    return remember { CatalogInteractionSource(FocusInteraction.Focus()) }
}

private class CatalogInteractionSource(initial: Interaction) : MutableInteractionSource {
    override val interactions = MutableSharedFlow<Interaction>(replay = 1).apply { tryEmit(initial) }
    override suspend fun emit(interaction: Interaction) = interactions.emit(interaction)
    override fun tryEmit(interaction: Interaction): Boolean = interactions.tryEmit(interaction)
}

// IDE previews are intentionally independent of the curated screenshot test matrix.

@DesignSystemCatalogThemeLocalePreviews
@Composable
private fun ActionsCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogActionsSurface()
    }
}

@DesignSystemCatalogThemeLocalePreviews
@Composable
private fun FieldsCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFieldsSurface()
    }
}

@DesignSystemCatalogThemeLocalePreviews
@Composable
private fun NavigationCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogNavigationSurface()
    }
}

@DesignSystemCatalogThemeLocalePreviews
@Composable
private fun FeedbackStatusCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackStatusSurface()
    }
}

@DesignSystemCatalogThemeLocalePreviews
@Composable
private fun FeedbackResolutionCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackResolutionSurface()
    }
}

@DesignSystemCatalogThemeLocalePreviews
@Composable
private fun FeedbackRecoveryCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackRecoverySurface()
    }
}

@DesignSystemCatalogThemeLocalePreviews
@Composable
private fun FeedbackAvailabilityCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackAvailabilitySurface()
    }
}

@DesignSystemCatalogThemeLocalePreviews
@Composable
private fun ConfirmationCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogConfirmationSurface()
    }
}

@Preview(
    name = "Fields compact 200 percent",
    group = "Design system catalog adaptation",
    widthDp = 360,
    heightDp = 640,
    fontScale = 2.0f,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun FieldsFontScale200CatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFieldsFontScale200Surface()
    }
}

@Preview(
    name = "Navigation medium landscape",
    group = "Design system catalog adaptation",
    widthDp = 700,
    heightDp = 480,
    fontScale = 1.0f,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun NavigationLandscapeCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogNavigationSurface()
    }
}

@Preview(
    name = "Feedback medium landscape",
    group = "Design system catalog adaptation",
    widthDp = 700,
    heightDp = 480,
    fontScale = 1.0f,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun FeedbackLandscapeCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackAvailabilitySurface()
    }
}

@Preview(
    name = "Confirmation compact 200 percent",
    group = "Design system catalog adaptation",
    widthDp = 360,
    heightDp = 640,
    fontScale = 2.0f,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ConfirmationFontScale200CatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogConfirmationSurface()
    }
}

@Preview(
    name = "Confirmation loading",
    group = "Design system catalog adaptation",
    widthDp = 360,
    heightDp = 640,
    fontScale = 1.0f,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun ConfirmationLoadingCatalogPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogConfirmationSurface(loading = true)
    }
}

