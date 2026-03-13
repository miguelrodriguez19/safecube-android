package com.miguelrodriguez19.safecube.app.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier


@Composable
fun SplashGateScreen() {
    LoadingGateScreen(message = LOADING_SESSION_MESSAGE)
}

@Composable
private fun LoadingGateScreen(message: String) {
    Column(
        modifier = Modifier.Companion.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Companion.CenterHorizontally,
    ) {
        CircularProgressIndicator()
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

private const val LOADING_SESSION_MESSAGE = "Loading session..."
