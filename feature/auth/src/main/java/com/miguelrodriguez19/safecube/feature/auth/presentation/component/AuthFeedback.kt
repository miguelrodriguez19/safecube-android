package com.miguelrodriguez19.safecube.feature.auth.presentation.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBanner
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeBannerVariant
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeRetryableError
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeTerminalError
import com.miguelrodriguez19.safecube.feature.auth.R
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.AuthUiOperationState
import com.miguelrodriguez19.safecube.feature.auth.presentation.state.isRetryable

@Composable
internal fun AuthFeedback(
    operationState: AuthUiOperationState,
    message: String?,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when {
        operationState == AuthUiOperationState.Loading -> Unit

        operationState.isRetryable() -> {
            SafeCubeRetryableError(
                title = stringResource(R.string.auth_retry_title),
                message = message ?: stringResource(R.string.auth_retry_message),
                onRetry = onRetry,
                modifier = modifier,
            )
        }

        operationState == AuthUiOperationState.InvalidCredentials ||
            operationState == AuthUiOperationState.AccountAlreadyExists ||
            operationState == AuthUiOperationState.ValidationError -> {
            message?.let {
                SafeCubeBanner(
                    message = it,
                    variant = SafeCubeBannerVariant.Error,
                    modifier = modifier,
                )
            }
        }

        operationState == AuthUiOperationState.Forbidden ||
            operationState == AuthUiOperationState.TerminalError -> {
            SafeCubeTerminalError(
                title = stringResource(R.string.auth_terminal_error_title),
                message = message ?: stringResource(R.string.auth_terminal_error_message),
                modifier = modifier,
            )
        }
    }
}
