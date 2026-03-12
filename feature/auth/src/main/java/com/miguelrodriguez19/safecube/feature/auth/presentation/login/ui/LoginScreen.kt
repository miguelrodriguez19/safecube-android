package com.miguelrodriguez19.safecube.feature.auth.presentation.login.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.action.LoginUiAction
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.event.LoginUiEvent
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.state.LoginUiState
import com.miguelrodriguez19.safecube.feature.auth.presentation.login.viewmodel.LoginViewModel

@Composable
fun LoginScreen(
    onSignup: () -> Unit,
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                LoginUiEvent.LoginSucceeded -> onLoginSuccess()
            }
        }
    }

    LoginContent(
        uiState = uiState,
        onAction = viewModel::onAction,
        onSignup = onSignup,
    )
}

@Composable
private fun LoginContent(
    uiState: LoginUiState,
    onAction: (LoginUiAction) -> Unit,
    onSignup: () -> Unit,
) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            Text(
                text = stringResource(R.string.access_your_vault),
                style = MaterialTheme.typography.headlineSmall,
            )
            OutlinedTextField(
                value = uiState.email,
                onValueChange = { onAction(LoginUiAction.EmailChanged(it)) },
                label = { Text(stringResource(R.string.email_label)) },
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                singleLine = true,
                isError = uiState.emailErrorRes != null,
                enabled = !uiState.isLoading,
            )
            uiState.emailErrorRes?.let { emailErrorRes ->
                Text(
                    text = stringResource(emailErrorRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            OutlinedTextField(
                value = uiState.password,
                onValueChange = { onAction(LoginUiAction.PasswordChanged(it)) },
                label = { Text(stringResource(R.string.password_label)) },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                singleLine = true,
                isError = uiState.passwordErrorRes != null,
                enabled = !uiState.isLoading,
            )
            uiState.passwordErrorRes?.let { passwordErrorRes ->
                Text(
                    text = stringResource(passwordErrorRes),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            uiState.errorMessageRes?.let { errorMessageRes ->
                Text(
                    text = stringResource(errorMessageRes),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
            Button(
                onClick = { onAction(LoginUiAction.Submit) },
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
            ) {
                Text(stringResource(if (uiState.isLoading) R.string.logging_in else R.string.login))
            }
            Button(
                onClick = onSignup,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(stringResource(R.string.go_to_signup))
            }
        }
    }
}
