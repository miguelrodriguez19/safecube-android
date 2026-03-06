package com.miguelrodriguez19.safecube.feature.auth.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import com.miguelrodriguez19.safecube.feature.auth.presentation.signup.SignupViewModel

@Composable
fun SignupScreen(
    onLogin: () -> Unit,
    onSignupSuccess: () -> Unit,
    viewModel: SignupViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.signupSucceeded) {
        if (uiState.signupSucceeded) {
            viewModel.consumeSignupSuccess()
            onSignupSuccess()
        }
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
        ) {
            Text(
                text = stringResource(R.string.start_your_journey),
                style = MaterialTheme.typography.headlineSmall,
            )
            OutlinedTextField(
                value = uiState.email,
                onValueChange = viewModel::onEmailChanged,
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
                onValueChange = viewModel::onPasswordChanged,
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
            OutlinedTextField(
                value = uiState.confirmPassword,
                onValueChange = viewModel::onConfirmPasswordChanged,
                label = { Text("Confirm password") },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
                singleLine = true,
                isError = uiState.confirmPasswordErrorRes != null,
                enabled = !uiState.isLoading,
            )
            uiState.confirmPasswordErrorRes?.let { confirmPasswordErrorRes ->
                Text(
                    text = stringResource(confirmPasswordErrorRes),
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
                onClick = viewModel::signup,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
            ) {
                Text(stringResource(
                    if (uiState.isLoading) R.string.creating_account else R.string.created_account))
            }
            Button(
                onClick = onLogin,
                enabled = !uiState.isLoading,
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            ) {
                Text(stringResource(R.string.already_registered))
            }
        }
    }
}
