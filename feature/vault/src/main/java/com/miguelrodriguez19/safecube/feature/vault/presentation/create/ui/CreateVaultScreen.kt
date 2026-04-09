package com.miguelrodriguez19.safecube.feature.vault.presentation.create.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.action.CreateVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.event.CreateVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.state.CreateVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.viewmodel.CreateVaultViewModel

@Composable
fun CreateVaultScreen(
    onRecoveryKey: (String) -> Unit,
    onVaultAlreadyExists: () -> Unit,
    viewModel: CreateVaultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is CreateVaultUiEvent.NavigateToRecoveryKey -> onRecoveryKey(event.recoveryKeyBase64)
                CreateVaultUiEvent.NavigateToUnlock -> onVaultAlreadyExists()
            }
        }
    }

    CreateVaultContent(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun CreateVaultContent(
    uiState: CreateVaultUiState,
    onAction: (CreateVaultUiAction) -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 2.dp,
                modifier = Modifier.statusBarsPadding(),
            ) {
                Text(
                    text = "Create Vault",
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
            Text("Step 1: initialize secure vault storage")
            Text(
                text = "Define your passphrase to initialize the vault.",
                modifier = Modifier.padding(bottom = 12.dp),
            )
            OutlinedTextField(
                value = uiState.passphrase,
                onValueChange = { onAction(CreateVaultUiAction.PassphraseChanged(it)) },
                label = { Text(stringResource(UiR.string.password_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.passphraseErrorRes != null,
                enabled = !uiState.isLoading,
            )
            uiState.passphraseErrorRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            uiState.errorMessageRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Button(
                onClick = { onAction(CreateVaultUiAction.Submit) },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (uiState.isLoading) "Creating vault..." else "Create vault")
            }
        }
    }
}
