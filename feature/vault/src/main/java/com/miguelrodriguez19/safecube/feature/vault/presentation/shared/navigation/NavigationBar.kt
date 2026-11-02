package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation

import androidx.annotation.StringRes
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIcons
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab.*

enum class AppTab(
    @param:StringRes val labelRes: Int,
    val icon: ImageVector
) {
    Vault(R.string.vault_label, SafeCubeIcons.Vault),
    Settings(R.string.settings_label, SafeCubeIcons.Settings),
}

@Composable
fun NavigationBar(
    selectedTab: AppTab,
    onVault: () -> Unit,
    onSettings: () -> Unit,
) {
    NavigationBar {
        NavigationBarItem(
            selected = selectedTab == Vault,
            onClick = onVault,
            icon = {
                Icon(
                    imageVector = Vault.icon,
                    contentDescription = stringResource(Vault.labelRes),
                )
            },
            label = { Text(stringResource(Vault.labelRes)) },
        )
        NavigationBarItem(
            selected = selectedTab == Settings,
            onClick = onSettings,
            icon = {
                Icon(
                    imageVector = Settings.icon,
                    contentDescription = stringResource(Settings.labelRes),
                )
            },
            label = { Text(stringResource(Settings.labelRes)) },
        )
    }
}
