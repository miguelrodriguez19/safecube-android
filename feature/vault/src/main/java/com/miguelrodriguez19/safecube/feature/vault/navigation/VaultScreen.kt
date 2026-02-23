package com.miguelrodriguez19.safecube.feature.vault.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun VaultScreen(
    onVault: () -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp) {
                Text(
                    text = "Vault",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        },
        bottomBar = {
            NavigationBar(
                selectedTab = AppTab.Vault,
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
        ) {
            Text(
                text = "Your vault entries",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Dummy content: encrypted notes, cards and documents will appear here.",
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
