package com.miguelrodriguez19.safecube.feature.auth.presentation.gate.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeProgress
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeRetryableError
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeScaffold
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTerminalError
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme
import com.miguelrodriguez19.safecube.feature.auth.R

@Composable
fun PostLoginGateScreen(
    isLoading: Boolean,
    @StringRes messageRes: Int,
    onRetry: () -> Unit,
) {
    val title = stringResource(R.string.vault_access_gate_title)

    SafeCubeScaffold(
        modifier = Modifier.semantics {
            isTraversalGroup = true
            paneTitle = title
        },
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val viewportHeight = maxHeight

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 560.dp)
                        .fillMaxWidth()
                        .heightIn(min = viewportHeight)
                        .padding(vertical = SafeCubeSpacing.Huge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.semantics { heading() },
                    )

                    if (isLoading) {
                        SafeCubeProgress(message = stringResource(messageRes))
                    } else {
                        GateFailure(
                            messageRes = messageRes,
                            onRetry = onRetry,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun GateFailure(
    @StringRes messageRes: Int,
    onRetry: () -> Unit,
) {
    val message = stringResource(messageRes)

    if (isRetryableGateMessage(messageRes)) {
        SafeCubeRetryableError(
            title = stringResource(R.string.vault_access_gate_retryable_title),
            message = message,
            onRetry = onRetry,
            modifier = Modifier.padding(top = SafeCubeSpacing.Large),
        )
    } else {
        SafeCubeTerminalError(
            title = stringResource(R.string.vault_access_gate_terminal_title),
            message = message,
            actionLabel = stringResource(UiR.string.retry),
            onAction = onRetry,
            modifier = Modifier.padding(top = SafeCubeSpacing.Large),
        )
    }
}

private fun isRetryableGateMessage(@StringRes messageRes: Int): Boolean =
    messageRes == UiR.string.vault_bootstrap_retryable_error ||
        messageRes == UiR.string.vault_error_retryable

@Preview(
    name = "Post-login gate loading · EN light",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Post-login gate loading · ES dark",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PostLoginGateLoadingPreview() {
    PostLoginGatePreviewContent(
        isLoading = true,
        messageRes = UiR.string.vault_bootstrap_loading,
    )
}

@Preview(
    name = "Post-login gate retryable · EN light",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Post-login gate retryable · ES dark",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PostLoginGateRetryablePreview() {
    PostLoginGatePreviewContent(
        isLoading = false,
        messageRes = UiR.string.vault_bootstrap_retryable_error,
    )
}

@Preview(
    name = "Post-login gate terminal · EN light",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "en",
    apiLevel = 36,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO,
)
@Preview(
    name = "Post-login gate terminal · ES dark",
    group = "SafeCube auth",
    widthDp = 360,
    heightDp = 640,
    locale = "es",
    apiLevel = 36,
    uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES,
)
@Composable
private fun PostLoginGateTerminalPreview() {
    PostLoginGatePreviewContent(
        isLoading = false,
        messageRes = UiR.string.vault_bootstrap_terminal_error,
    )
}

@Composable
private fun PostLoginGatePreviewContent(
    isLoading: Boolean,
    @StringRes messageRes: Int,
) {
    SafeCubeTheme(
        appearance = AppearanceSettings(
            mode = AppearanceMode.System,
            dynamicColorEnabled = false,
        ),
    ) {
        PostLoginGateScreen(
            isLoading = isLoading,
            messageRes = messageRes,
            onRetry = {},
        )
    }
}
