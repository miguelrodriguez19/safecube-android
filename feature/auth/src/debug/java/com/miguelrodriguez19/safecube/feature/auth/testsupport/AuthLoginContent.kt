package com.miguelrodriguez19.safecube.feature.auth.testsupport

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.state.LoginUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.ui.LoginContent
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.action.LoginUiAction

@Composable
fun AuthLoginContent(
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit,
    onSignup: () -> Unit,
    modifier: Modifier = Modifier,
    focusEffectsEnabled: Boolean = false,
) {
    LoginContent(
        uiState = uiState,
        onAction = onAction,
        onSignup = onSignup,
        modifier = modifier,
        focusEffectsEnabled = focusEffectsEnabled,
    )
}
