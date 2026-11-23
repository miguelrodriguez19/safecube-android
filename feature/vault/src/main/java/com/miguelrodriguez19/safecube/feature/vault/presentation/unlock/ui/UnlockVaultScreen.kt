package com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.ui

import android.content.res.Configuration
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBanner
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBannerVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButtonVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeConfirmationDialog
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeRetryableError
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTerminalError
import com.miguelrodriguez19.safecube.core.ui.component.SecretOutlinedTextField
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import com.miguelrodriguez19.safecube.core.vault.domain.session.VaultLockReason
import com.miguelrodriguez19.safecube.feature.vault.R
import com.miguelrodriguez19.safecube.feature.vault.presentation.component.VaultAccessLayout
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.findFragmentActivity
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.launchQuickUnlockPrompt
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.quickUnlockPromptCipherProvider
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.action.UnlockVaultUiAction
import com.miguelrodriguez19.safecube.feature.vault.presentation.unlock.event.UnlockVaultUiEvent
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
internal fun UnlockVaultContent(
    uiState: UnlockVaultUiState,
    onAction: (UnlockVaultUiAction) -> Unit,
    focusEffectsEnabled: Boolean = true,
) {
    val isInvalidPassphrase = uiState.errorMessageRes == UiR.string.vault_error_invalid_passphrase
    val passphraseErrorMessage = when {
        uiState.passphraseErrorRes == UiR.string.password_is_required ->
            stringResource(R.string.vault_access_unlock_passphrase_required)

        uiState.passphraseErrorRes != null -> stringResource(uiState.passphraseErrorRes)
        isInvalidPassphrase -> stringResource(UiR.string.vault_error_invalid_passphrase)
        else -> null
    }
    val passphraseFocusRequester = remember { FocusRequester() }
    val passphraseBringIntoViewRequester = remember { BringIntoViewRequester() }
    val windowFocused = LocalWindowInfo.current.isWindowFocused
    val isInspectionMode = LocalInspectionMode.current

    LaunchedEffect(
        focusEffectsEnabled,
        passphraseErrorMessage,
        uiState.isLoading,
        uiState.lockReason,
        uiState.showQuickUnlockOffer,
        windowFocused,
        isInspectionMode,
    ) {
        if (
            !focusEffectsEnabled ||
            isInspectionMode ||
            !windowFocused ||
            passphraseErrorMessage == null ||
            uiState.isLoading ||
            uiState.lockReason != null ||
            uiState.showQuickUnlockOffer
        ) {
            return@LaunchedEffect
        }

        withFrameNanos { }
        if (windowFocused && uiState.lockReason == null && !uiState.showQuickUnlockOffer) {
            passphraseFocusRequester.requestFocus()
            passphraseBringIntoViewRequester.bringIntoView()
        }
    }

    uiState.lockReason?.let { lockReason ->
        SafeCubeConfirmationDialog(
            title = stringResource(lockReason.titleRes()),
            message = stringResource(lockReason.messageRes()),
            confirmLabel = stringResource(R.string.vault_access_unlock_understood),
            onConfirm = { onAction(UnlockVaultUiAction.DismissLockNotice) },
            onDismiss = { onAction(UnlockVaultUiAction.DismissLockNotice) },
            showDismissAction = false,
            onDismissRequest = { onAction(UnlockVaultUiAction.DismissLockNotice) },
        )
    }
    if (uiState.lockReason == null && uiState.showQuickUnlockOffer) {
        SafeCubeConfirmationDialog(
            title = stringResource(R.string.vault_access_unlock_quick_unlock_offer_title),
            message = stringResource(R.string.vault_access_unlock_quick_unlock_offer_description),
            confirmLabel = stringResource(R.string.vault_access_unlock_quick_unlock_enable),
            onConfirm = { onAction(UnlockVaultUiAction.EnableQuickUnlock) },
            onDismiss = { onAction(UnlockVaultUiAction.DeclineQuickUnlock) },
            dismissLabel = stringResource(R.string.vault_access_unlock_quick_unlock_not_now),
            onDismissRequest = {},
        )
    }

    VaultAccessLayout(
        title = stringResource(R.string.vault_access_unlock_title),
        subtitle = stringResource(R.string.vault_access_unlock_subtitle),
    ) {
        SecretOutlinedTextField(
            value = uiState.passphrase,
            onValueChange = {
                if (!uiState.isLoading) {
                    onAction(UnlockVaultUiAction.PassphraseChanged(it))
                }
            },
            label = { Text(stringResource(R.string.vault_access_unlock_field_label)) },
            modifier = Modifier
                .fillMaxWidth()
                .bringIntoViewRequester(passphraseBringIntoViewRequester)
                .focusRequester(passphraseFocusRequester),
            isError = passphraseErrorMessage != null,
            errorMessage = passphraseErrorMessage,
            supportingText = {
                Text(
                    passphraseErrorMessage
                        ?: stringResource(R.string.vault_access_unlock_field_supporting),
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
                onDone = {
                    if (!uiState.isLoading) {
                        onAction(
                            if (uiState.isRetryable) {
                                UnlockVaultUiAction.Retry
                            } else {
                                UnlockVaultUiAction.Submit
                            },
                        )
                    }
                },
            ),
        )

        if (uiState.isRetryable && uiState.errorMessageRes != null) {
            SafeCubeRetryableError(
                title = stringResource(R.string.vault_access_unlock_retry_title),
                message = stringResource(uiState.errorMessageRes),
                onRetry = {
                    if (!uiState.isLoading) onAction(UnlockVaultUiAction.Retry)
                },
                retrying = uiState.isLoading,
            )
        } else {
            SafeCubeButton(
                text = stringResource(
                    if (uiState.isLoading) {
                        R.string.vault_access_unlock_loading
                    } else {
                        R.string.vault_access_unlock_action
                    },
                ),
                onClick = {
                    if (!uiState.isLoading) {
                        onAction(
                            if (uiState.isRetryable) {
                                UnlockVaultUiAction.Retry
                            } else {
                                UnlockVaultUiAction.Submit
                            },
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = SafeCubeSpacing.Small),
                enabled = !uiState.isLoading,
                loading = uiState.isLoading,
            )
        }

        if (uiState.canRetryQuickUnlock && uiState.errorMessageRes != null) {
            SafeCubeBanner(
                message = stringResource(uiState.errorMessageRes),
                variant = SafeCubeBannerVariant.Error,
                liveRegionMode = LiveRegionMode.Polite,
            )
        } else if (
            uiState.errorMessageRes != null &&
            !uiState.isRetryable &&
            !uiState.canRetryQuickUnlock &&
            !isInvalidPassphrase
        ) {
            SafeCubeTerminalError(
                title = stringResource(R.string.vault_access_unlock_error_title),
                message = stringResource(uiState.errorMessageRes),
            )
        }

        if (uiState.hasQuickUnlockEnrollment) {
            SafeCubeButton(
                text = stringResource(R.string.vault_access_unlock_quick_unlock_retry),
                onClick = {
                    if (!uiState.isLoading) onAction(UnlockVaultUiAction.RetryQuickUnlock)
                },
                modifier = Modifier.fillMaxWidth(),
                variant = SafeCubeButtonVariant.Secondary,
                enabled = !uiState.isLoading,
            )
        }
    }
}

private fun VaultLockReason.titleRes(): Int = when (this) {
    VaultLockReason.RemotePassphraseChanged ->
        UiR.string.vault_lock_remote_passphrase_changed_title

    VaultLockReason.PassphraseChangeReconciliationRequired ->
        UiR.string.vault_lock_passphrase_reconciliation_title
}

private fun VaultLockReason.messageRes(): Int = when (this) {
    VaultLockReason.RemotePassphraseChanged ->
        UiR.string.vault_lock_remote_passphrase_changed_message

    VaultLockReason.PassphraseChangeReconciliationRequired ->
        UiR.string.vault_lock_passphrase_reconciliation_message
}

@Preview(
    name = "Unlock · EN light",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun VaultUnlockEnglishLightPreview() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.Light,
            dynamicColorEnabled = false,
        ),
    ) {
        UnlockVaultContent(
            uiState = UnlockVaultUiState(),
            onAction = {},
            focusEffectsEnabled = false,
        )
    }
}

@Preview(
    name = "Unlock · ES dark",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun VaultUnlockSpanishDarkPreview() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.Dark,
            dynamicColorEnabled = false,
        ),
    ) {
        UnlockVaultContent(
            uiState = UnlockVaultUiState(),
            onAction = {},
            focusEffectsEnabled = false,
        )
    }
}
