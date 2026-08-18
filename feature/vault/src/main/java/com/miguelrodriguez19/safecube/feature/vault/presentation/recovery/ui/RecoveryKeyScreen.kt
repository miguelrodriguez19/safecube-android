package com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.action.RecoveryKeyUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.event.RecoveryKeyUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.state.RecoveryKeyUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.recovery.viewmodel.RecoveryKeyViewModel

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
private fun RecoveryKeyContent(
    uiState: RecoveryKeyUiState,
    onAction: (RecoveryKeyUiAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 2.dp,
                modifier = Modifier.statusBarsPadding(),
            ) {
                Text(
                    text = stringResource(UiR.string.vault_recovery_title),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(stringResource(UiR.string.vault_recovery_step))
            Text(
                text = uiState.recoveryKey.ifBlank {
                    stringResource(UiR.string.vault_recovery_unavailable)
                },
                modifier = Modifier.padding(bottom = 12.dp),
            )
            uiState.errorMessageRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Checkbox(
                    checked = uiState.isConfirmed,
                    onCheckedChange = {
                        onAction(RecoveryKeyUiAction.ConfirmationChanged(it))
                    },
                    enabled = !uiState.isLoading && uiState.recoveryKey.isNotBlank(),
                )
                Text(
                    text = stringResource(UiR.string.vault_recovery_save_confirmation),
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            if (uiState.isRetryable) {
                Button(
                    onClick = { onAction(RecoveryKeyUiAction.Retry) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(UiR.string.retry))
                }
            }
            Button(
                onClick = { onAction(RecoveryKeyUiAction.Continue) },
                enabled = uiState.canContinue && !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(UiR.string.vault_recovery_continue))
            }
        }
    }
}
