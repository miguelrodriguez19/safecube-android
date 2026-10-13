package com.miguelrodriguez19.safecube.visual

import androidx.compose.runtime.Composable
import com.android.tools.screenshot.PreviewTest
import com.miguelrodriguez19.safecube.app.presentation.theme.SafecubeandroidTheme
import com.miguelrodriguez19.safecube.app.testsupport.visual.VisualRegressionCanaryFixtures
import com.miguelrodriguez19.safecube.app.testsupport.visual.VisualRegressionCanarySurface

@PreviewTest
@SafeCubeThemePreviews
@Composable
fun visualRegressionCanaryThemePreview() {
    VisualRegressionCanaryPreviewContent()
}
@PreviewTest
@SafeCubeLocalePreviews
@Composable
fun visualRegressionCanaryLocalePreview() {
    VisualRegressionCanaryPreviewContent()
}

@PreviewTest
@SafeCubeConfigurationPreviews
@Composable
fun visualRegressionCanaryConfigurationPreview() {
    VisualRegressionCanaryPreviewContent()
}

@Composable
private fun VisualRegressionCanaryPreviewContent() {
    SafecubeandroidTheme(dynamicColor = false) {
        VisualRegressionCanarySurface(
            fixture = VisualRegressionCanaryFixtures.infrastructure,
            onAction = {},
        )
    }
}
