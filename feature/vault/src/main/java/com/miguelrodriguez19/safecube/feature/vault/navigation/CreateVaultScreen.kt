package com.miguelrodriguez19.safecube.feature.vault.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun CreateVaultScreen(onRecoveryKey: () -> Unit) {
    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp) {
                Text(
                    text = "Create Vault",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text("Step 1: initialize secure vault storage")
            Text(
                text = "This mock simulates vault creation with local encryption setup.",
                modifier = Modifier.padding(bottom = 12.dp),
            )
            Button(onClick = onRecoveryKey, modifier = Modifier.fillMaxWidth()) {
                Text("Continue to Recovery Key")
            }
        }
    }
}
