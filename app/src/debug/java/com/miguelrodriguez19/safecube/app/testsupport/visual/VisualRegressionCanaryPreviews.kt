package com.miguelrodriguez19.safecube.app.testsupport.visual

import androidx.compose.runtime.Composable

// Explore infrastructure configurations in the IDE without multiplying CI references.
@SafeCubeThemePreviews
@SafeCubeLocalePreviews
@SafeCubeConfigurationPreviews
@Composable
private fun VisualRegressionCanaryPreview() {
    DesignSystemCatalogTheme {
        VisualRegressionCanarySurface(
            fixture = VisualRegressionCanaryFixtures.infrastructure,
            onAction = {},
        )
    }
}
