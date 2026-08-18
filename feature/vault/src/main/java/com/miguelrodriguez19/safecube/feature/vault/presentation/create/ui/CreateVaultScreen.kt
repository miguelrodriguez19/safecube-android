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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.action.CreateVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.event.CreateVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.state.CreateVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.viewmodel.CreateVaultViewModel

@Composable
fun CreateVaultScreen(
    onRecoveryKey: () -> Unit,
    onVaultAlreadyExists: () -> Unit,
    viewModel: CreateVaultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                CreateVaultUiEvent.NavigateToRecoveryKey -> onRecoveryKey()
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
                    text = stringResource(UiR.string.vault_create_title),
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
            Text(stringResource(UiR.string.vault_create_step))
            Text(
                text = stringResource(UiR.string.vault_create_description),
                modifier = Modifier.padding(bottom = 12.dp),
            )
            OutlinedTextField(
                value = uiState.passphrase,
                onValueChange = { onAction(CreateVaultUiAction.PassphraseChanged(it)) },
                label = { Text(stringResource(UiR.string.password_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
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
                onClick = {
                    onAction(
                        if (uiState.isRetryable) {
                            CreateVaultUiAction.Retry
                        } else {
                            CreateVaultUiAction.Submit
                        },
                    )
                },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = when {
                        uiState.isLoading -> stringResource(UiR.string.vault_create_loading)
                        uiState.isRetryable -> stringResource(UiR.string.retry)
                        else -> stringResource(UiR.string.vault_create_action)
                    },
                )
            }
        }
    }
}
