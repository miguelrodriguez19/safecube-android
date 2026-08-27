package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.component.SecretOutlinedTextField
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action.UnlockVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.event.UnlockVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.findFragmentActivity
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.launchQuickUnlockPrompt
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.quickUnlockPromptCipherProvider
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.state.UnlockVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.viewmodel.UnlockVaultViewModel
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Composable
fun UnlockVaultScreen(
    onApp: () -> Unit,
    viewModel: UnlockVaultViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val activity = context.findFragmentActivity()
    val cipherProvider = runCatching { quickUnlockPromptCipherProvider(context) }.getOrNull()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                UnlockVaultUiEvent.NavigateToApp -> onApp()
                is UnlockVaultUiEvent.LaunchQuickUnlockPrompt -> Unit
            }
        }
    }

    LaunchedEffect(viewModel, activity, cipherProvider) {
        if (activity == null) return@LaunchedEffect
        activity.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
            viewModel.uiState
                .map { state -> state.pendingQuickUnlockPrompt to state.quickUnlockPromptPresented }
                .distinctUntilChanged()
                .collectLatest { (request, promptPresented) ->
                    if (request == null) return@collectLatest
                    if (cipherProvider == null) {
                        viewModel.onAction(
                            UnlockVaultUiAction.QuickUnlockPromptCancelled(request.operationId),
                        )
                    } else {
                        launchQuickUnlockPrompt(
                            activity = activity,
                            cipherProvider = cipherProvider,
                            request = request,
                            presentPrompt = !promptPresented,
                            onPresented = viewModel::onQuickUnlockPromptPresented,
                            onSucceeded = { operationId ->
                                viewModel.onAction(
                                    UnlockVaultUiAction.QuickUnlockPromptSucceeded(operationId),
                                )
                            },
                            onCancelledOrError = { operationId ->
                                viewModel.onAction(
                                    UnlockVaultUiAction.QuickUnlockPromptCancelled(operationId),
                                )
                            },
                        )
                    }
                }
        }
    }

    LaunchedEffect(viewModel, activity) {
        if (activity == null) {
            viewModel.onAction(UnlockVaultUiAction.ScreenEntered)
        } else {
            activity.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.onAction(UnlockVaultUiAction.ScreenEntered)
                awaitCancellation()
            }
        }
    }

    UnlockVaultContent(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
private fun UnlockVaultContent(
    uiState: UnlockVaultUiState,
    onAction: (UnlockVaultUiAction) -> Unit,
) {
    if (uiState.showQuickUnlockOffer) {
        AlertDialog(
            onDismissRequest = {},
            title = { Text(stringResource(UiR.string.quick_unlock_offer_title)) },
            text = { Text(stringResource(UiR.string.quick_unlock_offer_description)) },
            confirmButton = {
                TextButton(onClick = { onAction(UnlockVaultUiAction.EnableQuickUnlock) }) {
                    Text(stringResource(UiR.string.quick_unlock_enable))
                }
            },
            dismissButton = {
                TextButton(onClick = { onAction(UnlockVaultUiAction.DeclineQuickUnlock) }) {
                    Text(stringResource(UiR.string.quick_unlock_not_now))
                }
            },
        )
    }
    Scaffold(
        topBar = {
            Surface(
                shadowElevation = 2.dp,
                modifier = Modifier.statusBarsPadding(),
            ) {
                Text(
                    text = stringResource(UiR.string.vault_unlock_title),
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
            Text(stringResource(UiR.string.vault_unlock_description))
            SecretOutlinedTextField(
                value = uiState.passphrase,
                onValueChange = { onAction(UnlockVaultUiAction.PassphraseChanged(it)) },
                label = { Text(stringResource(UiR.string.password_label)) },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                isError = uiState.passphraseErrorRes != null,
                enabled = !uiState.isLoading,
            )
            uiState.passphraseErrorRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            Button(
                onClick = {
                    onAction(
                        if (uiState.isRetryable) {
                            UnlockVaultUiAction.Retry
                        } else {
                            UnlockVaultUiAction.Submit
                        },
                    )
                },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(
                    text = when {
                        uiState.isLoading -> stringResource(UiR.string.vault_unlock_loading)
                        uiState.isRetryable -> stringResource(UiR.string.retry)
                        else -> stringResource(UiR.string.vault_unlock_action)
                    },
                )
            }
            uiState.errorMessageRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            if (uiState.hasQuickUnlockEnrollment) {
                OutlinedButton(
                    onClick = { onAction(UnlockVaultUiAction.RetryQuickUnlock) },
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(UiR.string.quick_unlock_retry))
                }
            }
        }
    }
}
