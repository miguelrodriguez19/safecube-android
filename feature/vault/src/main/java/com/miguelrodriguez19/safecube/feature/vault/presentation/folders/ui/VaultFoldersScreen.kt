package com.miguelrodriguez19.safecube.feature.vault.presentation.folders.ui

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
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.NavigationBar

@Composable
fun VaultFoldersScreen(
    onVault: () -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp) {
                Text(
                    text = "Vault Folders",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        },
        bottomBar = {
            NavigationBar(
                selectedTab = AppTab.VaultFolders,
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
                text = "Organize your vault",
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "Dummy content: personal, work and shared folders.",
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}
