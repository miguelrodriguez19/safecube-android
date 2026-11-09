package com.miguelrodriguez19.safecube.visual

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.miguelrodriguez19.safecube.app.testsupport.visual.VisualRegressionCanaryFixtures
import com.miguelrodriguez19.safecube.app.testsupport.visual.VisualRegressionCanarySurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.VisualTokenCatalogSurface
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme

@PreviewTest
@Preview(
    name = "Compact portrait",
    group = "Configuration",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
)
@Composable
fun visualRegressionCanaryConfigurationPreview() {
    VisualRegressionCanaryPreviewContent()
}

@Composable
private fun VisualRegressionCanaryPreviewContent() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.System,
            dynamicColorEnabled = false,
        ),
    ) {
        VisualRegressionCanarySurface(
            fixture = VisualRegressionCanaryFixtures.infrastructure,
            onAction = {},
        )
    }
}

@PreviewTest
@Preview(
    name = "Light tokens",
    group = "Tokens",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun visualTokenCatalogLightPreview() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.Light,
            dynamicColorEnabled = false,
        ),
    ) {
        VisualTokenCatalogSurface()
    }
}

@PreviewTest
@Preview(
    name = "Dark tokens",
    group = "Tokens",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun visualTokenCatalogDarkPreview() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.Dark,
            dynamicColorEnabled = false,
        ),
    ) {
        VisualTokenCatalogSurface()
    }
}
