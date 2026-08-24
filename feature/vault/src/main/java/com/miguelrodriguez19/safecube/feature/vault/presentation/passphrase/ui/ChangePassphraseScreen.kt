package com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.component.SecretOutlinedTextField
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.action.ChangePassphraseUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.event.ChangePassphraseUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.state.ChangePassphraseUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.passphrase.viewmodel.ChangePassphraseViewModel

@Composable
fun ChangePassphraseScreen(
    onBack: () -> Unit,
    onUnlockVault: () -> Unit,
    viewModel: ChangePassphraseViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    var currentPassphrase by remember { mutableStateOf("") }
    var newPassphrase by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                ChangePassphraseUiEvent.ClearFields -> {
                    currentPassphrase = ""
                    newPassphrase = ""
                    confirmation = ""
                }

                ChangePassphraseUiEvent.NavigateToUnlock -> onUnlockVault()
            }
        }
    }

    ChangePassphraseContent(
        uiState = uiState,
        currentPassphrase = currentPassphrase,
        newPassphrase = newPassphrase,
        confirmation = confirmation,
        onCurrentPassphraseChanged = {
            currentPassphrase = it
            viewModel.onAction(ChangePassphraseUiAction.FieldsChanged)
        },
        onNewPassphraseChanged = {
            newPassphrase = it
            viewModel.onAction(ChangePassphraseUiAction.FieldsChanged)
        },
        onConfirmationChanged = {
            confirmation = it
            viewModel.onAction(ChangePassphraseUiAction.FieldsChanged)
        },
        onSubmit = {
            viewModel.onAction(
                ChangePassphraseUiAction.Submit(
                    currentPassphrase = currentPassphrase,
                    newPassphrase = newPassphrase,
                    confirmation = confirmation,
                ),
            )
        },
        onCancel = {
            currentPassphrase = ""
            newPassphrase = ""
            confirmation = ""
            onBack()
        },
    )
}

@Composable
private fun ChangePassphraseContent(
    uiState: ChangePassphraseUiState,
    currentPassphrase: String,
    newPassphrase: String,
    confirmation: String,
    onCurrentPassphraseChanged: (String) -> Unit,
    onNewPassphraseChanged: (String) -> Unit,
    onConfirmationChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onCancel: () -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 2.dp,
                modifier = Modifier.statusBarsPadding(),
            ) {
                Text(
                    text = stringResource(UiR.string.change_passphrase_title),
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
            Text(stringResource(UiR.string.change_passphrase_description))
            PassphraseField(
                value = currentPassphrase,
                labelRes = UiR.string.change_passphrase_current_label,
                errorRes = uiState.currentPassphraseErrorRes,
                enabled = !uiState.isLoading,
                onValueChange = onCurrentPassphraseChanged,
            )
            PassphraseField(
                value = newPassphrase,
                labelRes = UiR.string.change_passphrase_new_label,
                errorRes = uiState.newPassphraseErrorRes,
                enabled = !uiState.isLoading,
                onValueChange = onNewPassphraseChanged,
            )
            PassphraseField(
                value = confirmation,
                labelRes = UiR.string.change_passphrase_confirmation_label,
                errorRes = uiState.confirmationErrorRes,
                enabled = !uiState.isLoading,
                onValueChange = onConfirmationChanged,
            )
            Button(
                onClick = onSubmit,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = when {
                        uiState.isLoading -> stringResource(UiR.string.change_passphrase_loading)
                        uiState.isRetryable -> stringResource(UiR.string.retry)
                        else -> stringResource(UiR.string.change_passphrase_action)
                    },
                )
            }
            OutlinedButton(
                onClick = onCancel,
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(UiR.string.change_passphrase_cancel))
            }
            uiState.successMessageRes?.let { messageRes ->
                Text(
                    text = stringResource(messageRes),
                    color = MaterialTheme.colorScheme.primary,
                )
            }
            uiState.errorMessageRes?.let { messageRes ->
                Text(
                    text = stringResource(messageRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun PassphraseField(
    value: String,
    labelRes: Int,
    errorRes: Int?,
    enabled: Boolean,
    onValueChange: (String) -> Unit,
) {
    SecretOutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(stringResource(labelRes)) },
        modifier = Modifier.fillMaxWidth(),
        enabled = enabled,
        isError = errorRes != null,
        supportingText = errorRes?.let { resource ->
            { Text(stringResource(resource)) }
        },
    )
}
