package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab.*

enum class AppTab(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Vault(R.string.vault_label, Icons.Default.Home),
    VaultFolders(R.string.folders_label, Icons.Default.Folder),
    Settings(R.string.settings_label, Icons.Default.Settings),
}

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
