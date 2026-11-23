package com.miguelrodriguez19.safecube.feature.vault.presentation.create.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButtonVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeRetryableError
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTerminalError
import com.miguelrodriguez19.safecube.core.ui.component.SecretOutlinedTextField
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.feature.vault.R
import com.miguelrodriguez19.safecube.feature.vault.presentation.component.VaultAccessLayout
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.action.CreateVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.event.CreateVaultUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.state.CreateVaultUiState
import com.miguelrodriguez19.safecube.feature.vault.presentation.create.viewmodel.CreateVaultViewModel
import com.miguelrodriguez19.safecube.feature.vault.presentation.state.VaultUiOperationState

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

    CreateVaultScreenContent(
        uiState = uiState,
        onAction = viewModel::onAction,
    )
}

@Composable
internal fun CreateVaultScreenContent(
    uiState: CreateVaultUiState,
    onAction: (CreateVaultUiAction) -> Unit,
    focusEffectsEnabled: Boolean = true,
) {
    val passphraseError = if (uiState.passphraseErrorRes != null) {
        stringResource(R.string.vault_access_create_passphrase_required)
    } else {
        null
    }
    val passphraseFocusRequester = remember { FocusRequester() }
    val passphraseBringIntoViewRequester = remember { BringIntoViewRequester() }
    val windowFocused = LocalWindowInfo.current.isWindowFocused
    val isInspectionMode = LocalInspectionMode.current

    LaunchedEffect(
        focusEffectsEnabled,
        passphraseError,
        uiState.isLoading,
        windowFocused,
        isInspectionMode,
    ) {
        if (
            !focusEffectsEnabled ||
            isInspectionMode ||
            !windowFocused ||
            passphraseError == null ||
            uiState.isLoading
        ) {
            return@LaunchedEffect
        }

        withFrameNanos { }
        if (windowFocused && !uiState.isLoading && passphraseError != null) {
            passphraseFocusRequester.requestFocus()
            passphraseBringIntoViewRequester.bringIntoView()
        }
    }

    val submitOrRetry = {
        if (!uiState.isLoading) {
            when {
                uiState.isRetryable -> onAction(CreateVaultUiAction.Retry)
                uiState.operationState == VaultUiOperationState.Idle ||
                    uiState.passphraseErrorRes != null ->
                    onAction(CreateVaultUiAction.Submit)
            }
        }
    }

    VaultAccessLayout(
        title = stringResource(R.string.vault_access_create_title),
        subtitle = stringResource(R.string.vault_access_create_subtitle),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Large)) {
            Text(
                text = stringResource(R.string.vault_access_create_step),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary,
            )
            SecretOutlinedTextField(
                value = uiState.passphrase,
                onValueChange = {
                    if (!uiState.isLoading) {
                        onAction(CreateVaultUiAction.PassphraseChanged(it))
                    }
                },
                label = { Text(stringResource(R.string.vault_access_create_passphrase_label)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(passphraseBringIntoViewRequester)
                    .focusRequester(passphraseFocusRequester),
                isError = passphraseError != null,
                errorMessage = passphraseError,
                supportingText = {
                    Text(
                        text = passphraseError
                            ?: stringResource(R.string.vault_access_create_passphrase_hint),
                        color = if (passphraseError != null) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        },
                    )
                },
                allowReveal = true,
                enabled = !uiState.isLoading,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                    autoCorrectEnabled = false,
                ),
                keyboardActions = KeyboardActions(
                    onDone = { submitOrRetry() },
                ),
            )
            when {
                uiState.isRetryable && uiState.errorMessageRes != null -> {
                    SafeCubeRetryableError(
                        title = stringResource(R.string.vault_access_create_error_title),
                        message = stringResource(uiState.errorMessageRes),
                        onRetry = {
                            if (!uiState.isLoading && uiState.isRetryable) {
                                onAction(CreateVaultUiAction.Retry)
                            }
                        },
                        retrying = uiState.isLoading,
                    )
                }

                uiState.operationState == VaultUiOperationState.TerminalError &&
                    uiState.errorMessageRes != null -> {
                    SafeCubeTerminalError(
                        title = stringResource(R.string.vault_access_create_error_title),
                        message = stringResource(uiState.errorMessageRes),
                    )
                }

                uiState.operationState == VaultUiOperationState.Idle ||
                    uiState.operationState == VaultUiOperationState.Loading ||
                    uiState.passphraseErrorRes != null -> {
                    SafeCubeButton(
                        text = stringResource(
                            if (uiState.isLoading) {
                                R.string.vault_access_create_loading
                            } else {
                                R.string.vault_access_create_action
                            },
                        ),
                        onClick = { submitOrRetry() },
                        variant = SafeCubeButtonVariant.Primary,
                        enabled = !uiState.isLoading,
                        loading = uiState.isLoading,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Create vault · EN light",
    group = "SafeCube vault access",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Create vault · ES dark",
    group = "SafeCube vault access",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun CreateVaultScreenPreview() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.System,
            dynamicColorEnabled = false,
        ),
    ) {
        CreateVaultScreenContent(
            uiState = CreateVaultUiState(),
            onAction = {},
            focusEffectsEnabled = false,
        )
    }
}
