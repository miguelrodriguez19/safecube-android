package com.miguelrodriguez19.safecube.visual

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.miguelrodriguez19.safecube.app.testsupport.visual.ShellSettingsVisualSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.ShellVaultVisualSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.ShellVisualTheme
import com.miguelrodriguez19.safecube.app.presentation.ui.SplashGateScreen
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode

@PreviewTest
@Preview(
    name = "Splash light English",
    group = "SafeCube shell",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun shellSplashLightEnglishPreview() {
    ShellVisualTheme(AppearanceMode.Light) {
        SplashGateScreen()
    }
}

@PreviewTest
@Preview(
    name = "Splash dark English",
    group = "SafeCube shell",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun shellSplashDarkEnglishPreview() {
    ShellVisualTheme(AppearanceMode.Dark) {
        SplashGateScreen()
    }
}

@PreviewTest
@Preview(
    name = "Vault selected light English",
    group = "SafeCube shell",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun shellVaultSelectedLightEnglishPreview() {
    ShellVisualTheme(AppearanceMode.Light) {
        ShellVaultVisualSurface()
    }
}

@PreviewTest
@Preview(
    name = "Settings selected dark Spanish",
    group = "SafeCube shell",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun shellSettingsSelectedDarkSpanishPreview() {
    ShellVisualTheme(AppearanceMode.Dark) {
        ShellSettingsVisualSurface()
    }
}

@PreviewTest
@Preview(
    name = "Shell settings landscape light Spanish",
    group = "SafeCube shell adaptation",
    widthDp = 700,
    heightDp = 480,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun shellSettingsLandscapeLightSpanishPreview() {
    ShellVisualTheme(AppearanceMode.Light) {
        ShellSettingsVisualSurface()
    }
}

@PreviewTest
@Preview(
    name = "Shell vault 200 percent dark Spanish",
    group = "SafeCube shell adaptation",
    widthDp = 360,
    heightDp = 640,
    fontScale = 2f,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun shellVaultFontScale200DarkSpanishPreview() {
    ShellVisualTheme(AppearanceMode.Dark) {
        ShellVaultVisualSurface()
    }
}
