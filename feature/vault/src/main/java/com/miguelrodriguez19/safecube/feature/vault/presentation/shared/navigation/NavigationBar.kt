package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBottomNavigation
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeNavigationItem
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIcons

enum class AppTab(
    val id: String,
    @param:StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    Vault(
        id = "vault",
        labelRes = R.string.vault_label,
        icon = SafeCubeIcons.Vault,
    ),
    Settings(
        id = "settings",
        labelRes = R.string.settings_label,
        icon = SafeCubeIcons.Settings,
    ),
}

@Composable
fun NavigationBar(
    selectedTab: AppTab,
    onTabSelected: (AppTab) -> Unit,
    tabs: List<AppTab> = AppTab.entries,
) {
    require(tabs.isNotEmpty()) { "NavigationBar requires at least one tab" }
    require(selectedTab in tabs) { "selectedTab must be included in tabs" }

    SafeCubeBottomNavigation(
        items = tabs.map { tab ->
            SafeCubeNavigationItem(
                id = tab.id,
                label = stringResource(tab.labelRes),
                icon = tab.icon,
            )
        },
        selectedItemId = selectedTab.id,
        onItemClick = { selectedId ->
            tabs.firstOrNull { it.id == selectedId }
                ?.takeUnless { it == selectedTab }
                ?.let(onTabSelected)
        },
    )
}
