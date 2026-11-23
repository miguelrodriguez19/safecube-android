package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBanner
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBannerVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButtonVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeProgress
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeRecoveryKey
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeRetryableError
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTerminalError
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.feature.vault.R
import com.miguelrodriguez19.safecube.feature.vault.presentation.component.VaultAccessLayout
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.event.RecoveryKeyUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.viewmodel.RecoveryKeyViewModel
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState

@Composable
fun RecoveryKeyScreen(
    onUnlockVault: () -> Unit,
    viewModel: RecoveryKeyViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                RecoveryKeyUiEvent.ContinueToUnlock -> onUnlockVault()
            }
        }
    }

    RecoveryKeyContent(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun RecoveryKeyContent(
    uiState: RecoveryKeyUiState,
    onAction: (RecoveryKeyUiAction) -> Unit,
) {
    val errorMessage = uiState.errorMessageRes?.let { stringResource(it) }
    val hasRecoveryKey = uiState.recoveryKey.isNotBlank()

    VaultAccessLayout(
        title = stringResource(R.string.vault_access_recovery_title),
        subtitle = stringResource(R.string.vault_access_recovery_subtitle),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Medium)) {
            Text(
                text = stringResource(R.string.vault_access_recovery_step),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            Text(
                text = stringResource(R.string.vault_access_recovery_warning_title),
                style = MaterialTheme.typography.titleMedium,
            )
            SafeCubeBanner(
                message = stringResource(R.string.vault_access_recovery_warning_message),
                variant = SafeCubeBannerVariant.Warning,
                liveRegionMode = null,
                announceState = false,
            )

            when {
                uiState.isLoading -> {
                    SafeCubeProgress(
                        message = stringResource(R.string.vault_access_recovery_loading),
                    )
                }

                uiState.isRetryable -> {
                    SafeCubeRetryableError(
                        title = stringResource(R.string.vault_access_recovery_error_title),
                        message = errorMessage
                            ?: stringResource(UiR.string.vault_error_retryable),
                        onRetry = { onAction(RecoveryKeyUiAction.Retry) },
                    )
                }

                uiState.operationState == VaultUiOperationState.TerminalError || !hasRecoveryKey -> {
                    SafeCubeTerminalError(
                        title = stringResource(R.string.vault_access_recovery_error_title),
                        message = errorMessage
                            ?: stringResource(UiR.string.vault_recovery_unavailable),
                    )
                }

                else -> {
                    if (errorMessage != null) {
                        SafeCubeBanner(
                            message = errorMessage,
                            variant = SafeCubeBannerVariant.Error,
                        )
                    }
                    SafeCubeRecoveryKey(value = uiState.recoveryKey)
                    RecoveryKeyConfirmation(
                        isConfirmed = uiState.isConfirmed,
                        enabled = uiState.operationState == VaultUiOperationState.Success &&
                            hasRecoveryKey,
                        onCheckedChange = {
                            onAction(RecoveryKeyUiAction.ConfirmationChanged(it))
                        },
                    )
                }
            }

            SafeCubeButton(
                text = stringResource(R.string.vault_access_recovery_continue),
                onClick = { onAction(RecoveryKeyUiAction.Continue) },
                variant = SafeCubeButtonVariant.Primary,
                enabled = uiState.canContinue && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Composable
private fun RecoveryKeyConfirmation(
    isConfirmed: Boolean,
    enabled: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = SafeCubeSpacing.TouchTarget)
            .toggleable(
                value = isConfirmed,
                enabled = enabled,
                role = Role.Checkbox,
                onValueChange = onCheckedChange,
            )
            .padding(vertical = SafeCubeSpacing.ExtraSmall),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
    ) {
        Checkbox(
            checked = isConfirmed,
            onCheckedChange = null,
            enabled = enabled,
        )
        Text(
            text = stringResource(R.string.vault_access_recovery_confirmation),
            modifier = Modifier.weight(1f),
        )
    }
}

@Preview(
    name = "Recovery key · EN light",
    group = "SafeCube vault access",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Recovery key · ES dark",
    group = "SafeCube vault access",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun RecoveryKeyScreenPreview() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.System,
            dynamicColorEnabled = false,
        ),
    ) {
        RecoveryKeyContent(
            uiState = RecoveryKeyUiState(
                recoveryKey = "DEMO-NOT-A-RECOVERY-KEY",
                operationState = VaultUiOperationState.Success,
            ),
            onAction = {},
        )
    }
}
