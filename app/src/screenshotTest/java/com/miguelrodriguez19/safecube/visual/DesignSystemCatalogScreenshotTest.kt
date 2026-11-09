package com.miguelrodriguez19.safecube.visual

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogActionsSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogConfirmationSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogFeedbackAvailabilitySurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogFeedbackRecoverySurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogFeedbackResolutionSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogFeedbackStatusSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogFieldsFontScale200Surface
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogFieldsSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogNavigationSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.DesignSystemCatalogTheme

// Each golden must cover a distinct risk documented in docs/testing/visual-regression-testing.md.
// Development previews in debug deliberately use a broader matrix.
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "Light English",
    group = "Design system catalog",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark English",
    group = "Design system catalog",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class DesignSystemCatalogGoldenThemePreviews

@PreviewTest
@DesignSystemCatalogGoldenThemePreviews
@Composable
fun designSystemCatalogActionsPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogActionsSurface()
    }
}

@PreviewTest
@DesignSystemCatalogGoldenThemePreviews
@Composable
fun designSystemCatalogFieldsPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFieldsSurface()
    }
}

@PreviewTest
@DesignSystemCatalogGoldenThemePreviews
@Composable
fun designSystemCatalogNavigationPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogNavigationSurface()
    }
}

@PreviewTest
@DesignSystemCatalogGoldenThemePreviews
@Composable
fun designSystemCatalogFeedbackStatusPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackStatusSurface()
    }
}

@PreviewTest
@DesignSystemCatalogGoldenThemePreviews
@Composable
fun designSystemCatalogFeedbackResolutionPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackResolutionSurface()
    }
}

@PreviewTest
@DesignSystemCatalogGoldenThemePreviews
@Composable
fun designSystemCatalogFeedbackRecoveryPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackRecoverySurface()
    }
}

@PreviewTest
@DesignSystemCatalogGoldenThemePreviews
@Composable
fun designSystemCatalogFeedbackAvailabilityPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackAvailabilitySurface()
    }
}

@PreviewTest
@DesignSystemCatalogGoldenThemePreviews
@Composable
fun designSystemCatalogConfirmationPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogConfirmationSurface()
    }
}

@PreviewTest
@Preview(
    name = "Fields compact 200 percent",
    group = "Design system catalog adaptation",
    widthDp = 360,
    heightDp = 640,
    fontScale = 2.0f,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun designSystemCatalogFieldsFontScale200Preview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFieldsFontScale200Surface()
    }
}

@PreviewTest
@Preview(
    name = "Navigation medium landscape",
    group = "Design system catalog adaptation",
    widthDp = 700,
    heightDp = 480,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun designSystemCatalogNavigationLandscapePreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogNavigationSurface()
    }
}

@PreviewTest
@Preview(
    name = "Feedback medium landscape",
    group = "Design system catalog adaptation",
    widthDp = 700,
    heightDp = 480,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun designSystemCatalogFeedbackLandscapePreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFeedbackAvailabilitySurface()
    }
}

@PreviewTest
@Preview(
    name = "Confirmation compact 200 percent",
    group = "Design system catalog adaptation",
    widthDp = 360,
    heightDp = 640,
    fontScale = 2.0f,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
fun designSystemCatalogConfirmationFontScale200Preview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogConfirmationSurface()
    }
}

@PreviewTest
@Preview(
    name = "Confirmation loading",
    group = "Design system catalog states",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun designSystemCatalogConfirmationLoadingPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogConfirmationSurface(loading = true)
    }
}

// Translation/layout risk is sampled explicitly rather than multiplying every family by locale.
@PreviewTest
@Preview(
    name = "Light Spanish",
    group = "Design system catalog",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Composable
fun designSystemCatalogFieldsSpanishPreview() {
    DesignSystemCatalogTheme {
        DesignSystemCatalogFieldsSurface()
    }
}
