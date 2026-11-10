package com.miguelrodriguez19.safecube.feature.auth.presentation.welcome.ui

import android.content.res.Configuration
import androidx.annotation.FloatRange
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBanner
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBannerVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButton
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeButtonVariant
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.feature.auth.R
import com.miguelrodriguez19.safecube.feature.auth.presentation.AuthTestTags
import com.miguelrodriguez19.safecube.feature.auth.presentation.component.AuthScreenLayout

private val WELCOME_MARK_SIZE = 76.dp

@Composable
fun WelcomeScreen(
    onLogin: () -> Unit,
    onSignup: () -> Unit,
) {
    AuthScreenLayout(
        title = "",
        subtitle = "",
        modifier = Modifier.testTag(AuthTestTags.WELCOME_SCREEN),
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Large),
        ) {
            Icon(
                painter = painterResource(UiR.drawable.ic_safecube_mark),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(WELCOME_MARK_SIZE)
                    .align(Alignment.CenterHorizontally),
            )
            Text(
                text = stringResource(R.string.auth_brand_wordmark),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Text(
                text = stringResource(R.string.auth_welcome_title),
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.semantics { heading() },
            )
            Text(
                text = stringResource(R.string.auth_welcome_body),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            SafeCubeButton(
                text = stringResource(R.string.auth_login_action),
                onClick = onLogin,
                variant = SafeCubeButtonVariant.Primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(AuthTestTags.WELCOME_LOGIN_ACTION),
            )
            SafeCubeButton(
                text = stringResource(R.string.auth_signup_action),
                onClick = onSignup,
                variant = SafeCubeButtonVariant.Secondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag(AuthTestTags.WELCOME_SIGNUP_ACTION),
            )
            SafeCubeBanner(
                message = stringResource(R.string.auth_welcome_reassurance),
                variant = SafeCubeBannerVariant.Info,
            )
        }
    }
}

@Preview(
    name = "Welcome · EN light",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Welcome · ES dark",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun WelcomeScreenDevelopmentPreview() {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.System,
            dynamicColorEnabled = false,
        ),
    ) {
        WelcomeScreen(onLogin = {}, onSignup = {})
    }
}
