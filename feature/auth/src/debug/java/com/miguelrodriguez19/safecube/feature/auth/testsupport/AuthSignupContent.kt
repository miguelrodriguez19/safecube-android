package com.miguelrodriguez19.safecube.feature.auth.testsupport

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.action.SignupUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.state.SignupUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.ui.SignupContent

@Composable
fun AuthSignupContent(
    uiState: SignupUiState,
    onAction: (SignupUiAction) -> Unit,
    onLogin: () -> Unit,
    modifier: Modifier = Modifier,
    focusEffectsEnabled: Boolean = false,
) {
    SignupContent(
        uiState = uiState,
        onAction = onAction,
        onLogin = onLogin,
        modifier = modifier,
        focusEffectsEnabled = focusEffectsEnabled,
    )
}
