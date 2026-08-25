package com.miguelrodriguez19.safecube.feature.vault.presentation.settings.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.vault.domain.model.AutoLockTimeout
import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.findFragmentActivity
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.launchQuickUnlockPrompt
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.quickUnlockPromptCipherProvider
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.NavigationBar
import com.miguelrodriguez19.safecube.feature.vault.presentation.settings.event.SettingsUiEvent
import com.miguelrodriguez19.safecube.feature.vault.presentation.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onVault: () -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit,
    onLockNow: () -> Unit,
    onChangePassphrase: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val hasActiveDrafts by viewModel.hasActiveDrafts.collectAsState()
    val autoLockTimeout by viewModel.autoLockTimeout.collectAsState()
    val quickUnlockUiState by viewModel.quickUnlockUiState.collectAsState()
    val context = LocalContext.current
    val activity = context.findFragmentActivity()
    val cipherProvider = runCatching { quickUnlockPromptCipherProvider(context) }.getOrNull()
    var showDraftLogoutWarning by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                is SettingsUiEvent.LaunchQuickUnlockPrompt -> {
                    if (activity == null || cipherProvider == null) {
                        viewModel.onQuickUnlockPromptCancelled(event.request.operationId)
                    } else {
                        launchQuickUnlockPrompt(
                            activity = activity,
                            cipherProvider = cipherProvider,
                            request = event.request,
                            onSucceeded = viewModel::onQuickUnlockPromptSucceeded,
                            onCancelledOrError = viewModel::onQuickUnlockPromptCancelled,
                        )
                    }
                }
            }
        }
    }

    if (showDraftLogoutWarning) {
        AlertDialog(
            onDismissRequest = { showDraftLogoutWarning = false },
            title = {
                Text(stringResource(UiR.string.logout_with_drafts_title))
            },
            text = {
                Text(stringResource(UiR.string.logout_with_drafts_message))
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDraftLogoutWarning = false
                        onLogout()
                    },
                ) {
                    Text(stringResource(UiR.string.logout_discard_drafts))
                }
            },
            dismissButton = {
                TextButton(onClick = { showDraftLogoutWarning = false }) {
                    Text(stringResource(UiR.string.cancel))
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
                    text = stringResource(UiR.string.settings_label),
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        },
        bottomBar = {
            NavigationBar(
                selectedTab = AppTab.Settings,
                onVault = onVault,
                onVaultFolders = onVaultFolders,
                onSettings = onSettings,
            )
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = stringResource(UiR.string.settings_account_security),
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = stringResource(UiR.string.settings_auto_lock_description),
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Text(
                text = stringResource(UiR.string.settings_auto_lock_title),
                style = MaterialTheme.typography.titleMedium,
            )
            AutoLockTimeout.entries.forEach { timeout ->
                AutoLockTimeoutOption(
                    timeout = timeout,
                    selected = timeout == autoLockTimeout,
                    onSelected = { viewModel.setAutoLockTimeout(timeout) },
                )
            }
            Text(
                text = stringResource(UiR.string.settings_quick_unlock_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 12.dp),
            )
            Text(stringResource(UiR.string.settings_quick_unlock_description))
            Text(
                text = stringResource(quickUnlockUiState.offerState.statusRes()),
                style = MaterialTheme.typography.bodyMedium,
            )
            if (quickUnlockUiState.offerState == QuickUnlockOfferState.Enrolled) {
                OutlinedButton(
                    onClick = viewModel::disableQuickUnlock,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(UiR.string.settings_quick_unlock_disable))
                }
            } else {
                OutlinedButton(
                    onClick = viewModel::enableQuickUnlock,
                    enabled = quickUnlockUiState.offerState != QuickUnlockOfferState.Unsupported,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(UiR.string.settings_quick_unlock_enable))
                }
            }
            quickUnlockUiState.errorMessageRes?.let { errorRes ->
                Text(
                    text = stringResource(errorRes),
                    color = MaterialTheme.colorScheme.error,
                )
            }
            OutlinedButton(onClick = onLockNow, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(UiR.string.settings_lock_now))
            }
            OutlinedButton(
                onClick = onChangePassphrase,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(UiR.string.settings_change_passphrase))
            }
            Button(onClick = onProfile, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(UiR.string.settings_open_profile))
            }
            OutlinedButton(
                onClick = {
                    if (hasActiveDrafts == true) {
                        showDraftLogoutWarning = true
                    } else {
                        onLogout()
                    }
                },
                enabled = hasActiveDrafts != null,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(UiR.string.settings_log_out))
            }
        }
    }
}

@Composable
private fun AutoLockTimeoutOption(
    timeout: AutoLockTimeout,
    selected: Boolean,
    onSelected: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .selectable(
                selected = selected,
                onClick = onSelected,
                role = Role.RadioButton,
            )
            .padding(vertical = 2.dp),
    ) {
        RadioButton(
            selected = selected,
            onClick = null,
        )
        Text(
            text = stringResource(timeout.labelRes()),
            modifier = Modifier.padding(start = 8.dp, top = 12.dp),
        )
    }
}

private fun AutoLockTimeout.labelRes(): Int = when (this) {
    AutoLockTimeout.Immediately -> UiR.string.settings_auto_lock_immediately
    AutoLockTimeout.ThirtySeconds -> UiR.string.settings_auto_lock_30_seconds
    AutoLockTimeout.OneMinute -> UiR.string.settings_auto_lock_1_minute
    AutoLockTimeout.FiveMinutes -> UiR.string.settings_auto_lock_5_minutes
    AutoLockTimeout.FifteenMinutes -> UiR.string.settings_auto_lock_15_minutes
}

private fun QuickUnlockOfferState.statusRes(): Int = when (this) {
    QuickUnlockOfferState.Enrolled -> UiR.string.settings_quick_unlock_status_enabled
    QuickUnlockOfferState.Unsupported -> UiR.string.settings_quick_unlock_status_unsupported
    else -> UiR.string.settings_quick_unlock_status_disabled
}
