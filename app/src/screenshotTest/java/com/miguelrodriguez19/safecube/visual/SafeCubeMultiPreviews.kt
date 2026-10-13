package com.miguelrodriguez19.safecube.visual

import android.content.res.Configuration
import androidx.compose.ui.tooling.preview.Preview

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "Light",
    group = "Theme",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Dark",
    group = "Theme",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
annotation class SafeCubeThemePreviews

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "English",
    group = "Locale",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
)
@Preview(
    name = "Spanish",
    group = "Locale",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
)
annotation class SafeCubeLocalePreviews

@Target(AnnotationTarget.FUNCTION, AnnotationTarget.ANNOTATION_CLASS)
@Retention(AnnotationRetention.BINARY)
@Preview(
    name = "Compact portrait",
    group = "Configuration",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
)
@Preview(
    name = "Medium landscape",
    group = "Configuration",
    widthDp = 700,
    heightDp = 480,
    locale = "en",
    apiLevel = 36,
)
@Preview(
    name = "Compact font 200 percent",
    group = "Configuration",
    widthDp = 360,
    heightDp = 640,
    fontScale = 2.0f,
    locale = "en",
    apiLevel = 36,
)
annotation class SafeCubeConfigurationPreviews
