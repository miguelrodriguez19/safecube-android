package com.miguelrodriguez19.safecube.visual

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest
import com.miguelrodriguez19.safecube.app.testsupport.visual.AuthLoginVisualSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.AuthSignupVisualSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.AuthVisualTheme
import com.miguelrodriguez19.safecube.app.testsupport.visual.AuthWelcomeVisualSurface
import com.miguelrodriguez19.safecube.app.testsupport.visual.LoginVisualState
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode

@PreviewTest
@Preview(name = "Welcome light English", group = "SafeCube auth", widthDp = 360, heightDp = 640, locale = "en", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun authWelcomeLightEnglishPreview() {
    AuthVisualTheme(AppearanceMode.Light) { AuthWelcomeVisualSurface() }
}

@PreviewTest
@Preview(name = "Welcome dark Spanish", group = "SafeCube auth", widthDp = 360, heightDp = 640, locale = "es", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun authWelcomeDarkSpanishPreview() {
    AuthVisualTheme(AppearanceMode.Dark) { AuthWelcomeVisualSurface() }
}

@PreviewTest
@Preview(name = "Welcome light Spanish compact 200 percent", group = "SafeCube auth adaptations", widthDp = 360, heightDp = 640, fontScale = 2.0f, locale = "es", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun authWelcomeLightSpanishCompact200Preview() {
    AuthVisualTheme(AppearanceMode.Light) { AuthWelcomeVisualSurface() }
}

@PreviewTest
@Preview(name = "Login idle light English", group = "SafeCube auth", widthDp = 360, heightDp = 640, locale = "en", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun authLoginIdleLightEnglishPreview() {
    AuthVisualTheme(AppearanceMode.Light) { AuthLoginVisualSurface(LoginVisualState.Idle) }
}

@PreviewTest
@Preview(name = "Login loading light English", group = "SafeCube auth states", widthDp = 360, heightDp = 640, locale = "en", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun authLoginLoadingLightEnglishPreview() {
    AuthVisualTheme(AppearanceMode.Light) { AuthLoginVisualSurface(LoginVisualState.Loading) }
}

@PreviewTest
@Preview(name = "Login retryable dark Spanish", group = "SafeCube auth states", widthDp = 360, heightDp = 640, locale = "es", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun authLoginRetryableDarkSpanishPreview() {
    AuthVisualTheme(AppearanceMode.Dark) { AuthLoginVisualSurface(LoginVisualState.Retryable) }
}

@PreviewTest
@Preview(name = "Login terminal light English", group = "SafeCube auth states", widthDp = 360, heightDp = 640, locale = "en", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun authLoginTerminalLightEnglishPreview() {
    AuthVisualTheme(AppearanceMode.Light) { AuthLoginVisualSurface(LoginVisualState.Terminal) }
}

@PreviewTest
@Preview(name = "Login medium landscape dark Spanish", group = "SafeCube auth adaptations", widthDp = 700, heightDp = 480, locale = "es", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun authLoginMediumLandscapeDarkSpanishPreview() {
    AuthVisualTheme(AppearanceMode.Dark) { AuthLoginVisualSurface(LoginVisualState.Idle) }
}

@PreviewTest
@Preview(name = "Signup validation light Spanish", group = "SafeCube auth states", widthDp = 360, heightDp = 640, locale = "es", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun authSignupValidationLightSpanishPreview() {
    AuthVisualTheme(AppearanceMode.Light) { AuthSignupVisualSurface() }
}

@PreviewTest
@Preview(name = "Signup compact 200 percent dark Spanish", group = "SafeCube auth adaptations", widthDp = 360, heightDp = 640, fontScale = 2.0f, locale = "es", apiLevel = 36, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun authSignupCompact200DarkSpanishPreview() {
    AuthVisualTheme(AppearanceMode.Dark) { AuthSignupVisualSurface() }
}
