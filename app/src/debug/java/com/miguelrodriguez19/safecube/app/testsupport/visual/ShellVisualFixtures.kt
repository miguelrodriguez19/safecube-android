package com.miguelrodriguez19.safecube.app.testsupport.visual

import android.content.res.Configuration
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelrodriguez19.safecube.R
import com.miguelrodriguez19.safecube.app.presentation.ui.SplashGateScreen
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButtonVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeCard
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeIconButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeScaffold
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTopAppBar
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIcons
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.AppTab
import com.miguelrodriguez19.safecube.feature.vault.presentation.shared.navigation.NavigationBar
import androidx.compose.foundation.layout.RowScope

/**
 * Debug-only shell surfaces. Every value is synthetic and is safe for IDE previews and goldens.
 */
@Composable
fun ShellVaultVisualSurface() {
    ShellScaffoldSurface(
        selectedTab = AppTab.Vault,
        title = R.string.shell_visual_vault_title,
        topBarActions = {
            SafeCubeIconButton(
                imageVector = SafeCubeIcons.Sync,
                contentDescription = stringResource(R.string.shell_visual_sync_action),
                onClick = {},
            )
        },
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Medium),
        ) {
            Text(
                text = stringResource(R.string.shell_visual_vault_description),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            )
            SafeCubeCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.shell_visual_item_title),
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                )
                Text(stringResource(R.string.shell_visual_item_supporting))
                Text(stringResource(R.string.shell_visual_item_state))
            }
            SafeCubeButton(
                text = stringResource(R.string.shell_visual_vault_action),
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = SafeCubeButtonVariant.Primary,
            )
        }
    }
}

@Composable
fun ShellSettingsVisualSurface() {
    ShellScaffoldSurface(
        selectedTab = AppTab.Settings,
        title = R.string.shell_visual_settings_title,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Medium),
        ) {
            Text(
                text = stringResource(R.string.shell_visual_settings_description),
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
            )
            SafeCubeCard(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = stringResource(R.string.shell_visual_settings_card_title),
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                )
                Text(stringResource(R.string.shell_visual_settings_card_supporting))
            }
            SafeCubeButton(
                text = stringResource(R.string.shell_visual_settings_action),
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                variant = SafeCubeButtonVariant.Secondary,
            )
        }
    }
}

@Composable
private fun ShellScaffoldSurface(
    selectedTab: AppTab,
    @StringRes title: Int,
    topBarActions: @Composable RowScope.() -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    SafeCubeScaffold(
        topBar = {
            SafeCubeTopAppBar(
                title = stringResource(title),
                actions = topBarActions,
            )
        },
        bottomBar = {
            NavigationBar(
                selectedTab = selectedTab,
                onTabSelected = {},
            )
        },
        content = content,
    )
}

@Composable
fun ShellVisualTheme(
    mode: AppearanceMode,
    content: @Composable () -> Unit,
) {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = mode,
            dynamicColorEnabled = false,
        ),
        content = content,
    )
}

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "Shell light English",
    group = "SafeCube shell development",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Shell dark English",
    group = "SafeCube shell development",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Preview(
    name = "Shell light Spanish",
    group = "SafeCube shell development",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Shell dark Spanish",
    group = "SafeCube shell development",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class ShellVisualDevelopmentPreviews

@ShellVisualDevelopmentPreviews
@Composable
private fun ShellSplashFixtureDevelopmentPreview() {
    ShellVisualTheme(AppearanceMode.System) {
        SplashGateScreen()
    }
}

@ShellVisualDevelopmentPreviews
@Composable
private fun ShellVaultFixtureDevelopmentPreview() {
    ShellVisualTheme(AppearanceMode.System) {
        ShellVaultVisualSurface()
    }
}

@ShellVisualDevelopmentPreviews
@Composable
private fun ShellSettingsFixtureDevelopmentPreview() {
    ShellVisualTheme(AppearanceMode.System) {
        ShellSettingsVisualSurface()
    }
}

@Preview(
    name = "Shell settings landscape light Spanish",
    group = "SafeCube shell development adaptation",
    widthDp = 700,
    heightDp = 480,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
private fun ShellSettingsLandscapeFixtureDevelopmentPreview() {
    ShellVisualTheme(AppearanceMode.Light) {
        ShellSettingsVisualSurface()
    }
}

@Preview(
    name = "Shell vault font scale 200 dark Spanish",
    group = "SafeCube shell development adaptation",
    widthDp = 360,
    heightDp = 640,
    fontScale = 2f,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun ShellVaultFontScaleFixtureDevelopmentPreview() {
    ShellVisualTheme(AppearanceMode.Dark) {
        ShellVaultVisualSurface()
    }
}
