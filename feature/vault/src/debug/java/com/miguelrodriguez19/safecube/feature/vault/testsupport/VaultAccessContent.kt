package com.miguelrodriguez19.safecube.feature.vault.testsupport

import androidx.compose.runtime.Composable
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.action.CreateVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.state.CreateVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.ui.CreateVaultScreenContent
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.ui.RecoveryKeyContent

@Composable
fun VaultCreateContent(
    uiState: CreateVaultUiState,
    onAction: (CreateVaultUiAction) -> Unit,
    focusEffectsEnabled: Boolean = true,
) {
    CreateVaultScreenContent(
        uiState = uiState,
        onAction = onAction,
        focusEffectsEnabled = focusEffectsEnabled,
    )
}

@Composable
fun VaultRecoveryContent(
    uiState: RecoveryKeyUiState,
    onAction: (RecoveryKeyUiAction) -> Unit,
) {
    RecoveryKeyContent(
        uiState = uiState,
        onAction = onAction,
    )
}
