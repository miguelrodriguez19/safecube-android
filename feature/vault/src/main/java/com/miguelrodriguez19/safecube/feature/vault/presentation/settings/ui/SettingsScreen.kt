package com.miguelrodriguez19.safecube.feature.vault.presentation.settings.ui

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
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.NavigationBar
import com.miguelrodriguez19.safecube.feature.vault.presentation.settings.viewmodel.SettingsViewModel

@Composable
fun SettingsScreen(
    onVault: () -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit,
    viewModel: SettingsViewModel = hiltViewModel(),
) {
    val hasActiveDrafts by viewModel.hasActiveDrafts.collectAsState()
    var showDraftLogoutWarning by remember { mutableStateOf(false) }

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
                    text = "Settings",
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
                text = "Account and security",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Dummy content: session duration, biometrics, and security preferences.",
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Button(onClick = onProfile, modifier = Modifier.fillMaxWidth()) {
                Text("Open Profile")
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
                Text("Log out")
            }
        }
    }
}
