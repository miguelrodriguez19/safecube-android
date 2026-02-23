package com.miguelrodriguez19.safecube.feature.vault.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SettingsScreen(
    onVault: () -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
    onProfile: () -> Unit,
    onLogout: () -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp) {
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
            OutlinedButton(onClick = onLogout, modifier = Modifier.fillMaxWidth()) {
                Text("Log out")
            }
        }
    }
}
