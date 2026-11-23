package com.miguelrodriguez19.safecube.app.testsupport.visual

import androidx.compose.runtime.Composable
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultLockReason
import com.miguelrodriguez19.safecube.feature.auth.presentation.gate.ui.PostLoginGateScreen
import com.miguelrodriguez19.safecube.feature.vault.R
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.state.CreateVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptOperation
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.state.UnlockVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.testsupport.VaultCreateContent
import com.miguelrodriguez19.safecube.feature.vault.testsupport.VaultRecoveryContent
import com.miguelrodriguez19.safecube.feature.vault.testsupport.VaultUnlockContent

enum class VaultAccessVisualCase {
    GateLoading,
    GateRetryable,
    GateTerminal,
    CreateIdle,
    CreateLoading,
    CreateValidation,
    RecoveryHidden,
    RecoveryConfirmed,
    RecoveryConfirmationError,
    UnlockIdle,
    UnlockLoading,
    UnlockError,
    UnlockQuickUnlockOffer,
    UnlockQuickUnlockFallback,
    UnlockRemotePassphraseChanged,
    UnlockPassphraseReconciliationRequired,
}

@Composable
fun VaultAccessVisualSurface(case: VaultAccessVisualCase) {
    val owner = androidx.compose.runtime.remember {
        object : androidx.lifecycle.LifecycleOwner {
            override val lifecycle = androidx.lifecycle.LifecycleRegistry.createUnsafe(this).apply {
                currentState = androidx.lifecycle.Lifecycle.State.RESUMED
            }
        }
    }
    androidx.compose.runtime.CompositionLocalProvider(
        androidx.lifecycle.compose.LocalLifecycleOwner provides owner,
    ) {
        VaultAccessVisualContent(case)
    }
}

@Composable
private fun VaultAccessVisualContent(case: VaultAccessVisualCase) {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.System,
            dynamicColorEnabled = false,
        ),
    ) {
        when (case) {
            VaultAccessVisualCase.GateLoading -> PostLoginGateScreen(
                isLoading = true,
                messageRes = UiR.string.vault_bootstrap_loading,
                onRetry = {},
            )

            VaultAccessVisualCase.GateRetryable -> PostLoginGateScreen(
                isLoading = false,
                messageRes = UiR.string.vault_bootstrap_retryable_error,
                onRetry = {},
            )

            VaultAccessVisualCase.GateTerminal -> PostLoginGateScreen(
                isLoading = false,
                messageRes = UiR.string.vault_bootstrap_terminal_error,
                onRetry = {},
            )

            VaultAccessVisualCase.CreateIdle -> VaultCreateContent(
                uiState = CreateVaultUiState(),
                onAction = {},
            )

            VaultAccessVisualCase.CreateLoading -> VaultCreateContent(
                uiState = CreateVaultUiState(operationState = VaultUiOperationState.Loading),
                onAction = {},
            )

            VaultAccessVisualCase.CreateValidation -> VaultCreateContent(
                uiState = CreateVaultUiState(
                    passphraseErrorRes = R.string.vault_access_create_passphrase_required,
                ),
                onAction = {},
            )

            VaultAccessVisualCase.RecoveryHidden -> VaultRecoveryContent(
                uiState = recoveryState(),
                onAction = {},
            )

            VaultAccessVisualCase.RecoveryConfirmed -> VaultRecoveryContent(
                uiState = recoveryState(isConfirmed = true),
                onAction = {},
            )

            VaultAccessVisualCase.RecoveryConfirmationError -> VaultRecoveryContent(
                uiState = recoveryState(
                    operationState = VaultUiOperationState.RetryableError,
                    isConfirmed = true,
                    errorMessageRes = UiR.string.vault_recovery_confirmation_failed,
                ),
                onAction = {},
            )

            VaultAccessVisualCase.UnlockIdle -> VaultUnlockContent(
                uiState = UnlockVaultUiState(),
                onAction = {},
            )

            VaultAccessVisualCase.UnlockLoading -> VaultUnlockContent(
                uiState = UnlockVaultUiState(operationState = VaultUiOperationState.Loading),
                onAction = {},
            )

            VaultAccessVisualCase.UnlockError -> VaultUnlockContent(
                uiState = UnlockVaultUiState(
                    operationState = VaultUiOperationState.RetryableError,
                    errorMessageRes = UiR.string.vault_error_retryable,
                ),
                onAction = {},
            )

            VaultAccessVisualCase.UnlockQuickUnlockOffer -> VaultUnlockContent(
                uiState = UnlockVaultUiState(showQuickUnlockOffer = true),
                onAction = {},
            )

            VaultAccessVisualCase.UnlockQuickUnlockFallback -> VaultUnlockContent(
                uiState = UnlockVaultUiState(
                    errorMessageRes = UiR.string.quick_unlock_error,
                    hasQuickUnlockEnrollment = true,
                    canRetryQuickUnlock = true,
                ),
                onAction = {},
            )

            VaultAccessVisualCase.UnlockRemotePassphraseChanged -> VaultUnlockContent(
                uiState = UnlockVaultUiState(
                    lockReason = VaultLockReason.RemotePassphraseChanged,
                ),
                onAction = {},
            )

            VaultAccessVisualCase.UnlockPassphraseReconciliationRequired -> VaultUnlockContent(
                uiState = UnlockVaultUiState(
                    lockReason = VaultLockReason.PassphraseChangeReconciliationRequired,
                ),
                onAction = {},
            )
        }
    }
}

private fun recoveryState(
    operationState: VaultUiOperationState = VaultUiOperationState.Success,
    isConfirmed: Boolean = false,
    errorMessageRes: Int? = null,
) = RecoveryKeyUiState(
    recoveryKey = DEMO_RECOVERY_KEY,
    operationState = operationState,
    isConfirmed = isConfirmed,
    errorMessageRes = errorMessageRes,
)

private const val DEMO_RECOVERY_KEY = "DEMO-NOT-A-RECOVERY-KEY"
