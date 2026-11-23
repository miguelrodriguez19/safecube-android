package com.miguelrodriguez19.safecube.feature.vault.testsupport

import androidx.compose.runtime.Composable
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action.UnlockVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.state.UnlockVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.ui.UnlockVaultContent

@Composable
fun VaultUnlockContent(
    uiState: UnlockVaultUiState,
    onAction: (UnlockVaultUiAction) -> Unit,
    focusEffectsEnabled: Boolean = false,
) {
    UnlockVaultContent(
        uiState = uiState,
        onAction = onAction,
        focusEffectsEnabled = focusEffectsEnabled,
    )
}
