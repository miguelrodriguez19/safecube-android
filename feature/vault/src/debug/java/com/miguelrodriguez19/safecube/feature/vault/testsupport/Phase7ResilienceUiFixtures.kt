package com.miguelrodriguez19.safecube.feature.vault.testsupport

import androidx.compose.runtime.Composable
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.state.VaultHomeUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.home.ui.VaultHomeContent
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.ui.RecoveryKeyContent
import java.util.UUID

/** Debug-only rendering bridge for instrumented resilience assertions. */
@Composable
fun Phase7VaultHomeContent(
    uiState: VaultHomeUiState,
    onCreatePassword: () -> Unit,
    onCreateNote: () -> Unit,
    onEditPassword: (UUID) -> Unit,
    onEditNote: (UUID) -> Unit,
    onVault: () -> Unit,
    onSettings: () -> Unit,
    onSyncNow: () -> Unit,
) {
    VaultHomeContent(
        uiState = uiState,
        onCreatePassword = onCreatePassword,
        onCreateNote = onCreateNote,
        onEditPassword = onEditPassword,
        onEditNote = onEditNote,
        onVault = onVault,
        onSettings = onSettings,
        onSyncNow = onSyncNow,
    )
}

/** Debug-only rendering bridge for the pending-recovery state. */
@Composable
fun Phase7RecoveryKeyContent(
    uiState: RecoveryKeyUiState,
    onAction: (RecoveryKeyUiAction) -> Unit,
) {
    RecoveryKeyContent(
        uiState = uiState,
        onAction = onAction,
    )
}
