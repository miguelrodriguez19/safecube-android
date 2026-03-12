package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.miguelrodriguez19.safecube.core.ui.R

enum class AppTab(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Vault(R.string.vault_label, Icons.Default.Home),
    VaultFolders(R.string.folders_label, Icons.Default.Folder),
    Settings(R.string.settings_label, Icons.Default.Settings),
}