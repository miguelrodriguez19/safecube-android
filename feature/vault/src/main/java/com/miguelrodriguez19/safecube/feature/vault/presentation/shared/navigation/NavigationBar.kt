package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab.*

@Composable
fun NavigationBar(
    selectedTab: AppTab,
    onVault: () -> Unit,
    onVaultFolders: () -> Unit,
    onSettings: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == Vault,
            onClick = onVault,
            icon = { Icon(Vault.icon, "Vault") },
            label = { Text(stringResource(Vault.labelRes)) },
        )
        NavigationBarItem(
            selected = selectedTab == VaultFolders,
            onClick = onVaultFolders,
            icon = { Icon(VaultFolders.icon, "Vault Folders") },
            label = { Text(stringResource(VaultFolders.labelRes)) },
        )
        NavigationBarItem(
            selected = selectedTab == Settings,
            onClick = onSettings,
            icon = { Icon(Settings.icon, "Settings") },
            label = { Text(stringResource(Settings.labelRes)) },
        )
    }
}
