package com.miguelrodriguez19.safecube.feature.vault.navigation

import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.miguelrodriguez19.safecube.core.vault.domain.model.unlock.VaultUnlockError

@Composable
fun UnlockVaultScreen(
    onUnlockWithPassphrase: (String) -> VaultUnlockError?,
    onApp: () -> Unit,
    mapErrorToMessage: (VaultUnlockError) -> String,
) {
    var passphrase by rememberSaveable { mutableStateOf("") }
    var unlockErrorMessage by rememberSaveable { mutableStateOf<String?>(null) }

    Scaffold(
        topBar = {
            Surface(shadowElevation = 2.dp) {
                Text(
                    text = "Unlock Vault",
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
            Text("Authenticate to open your secure data")
            OutlinedTextField(
                value = passphrase,
                onValueChange = { passphrase = it },
                label = { Text("Master password") },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .fillMaxWidth(),
                singleLine = true,
            )
            Button(
                onClick = {
                    val error = onUnlockWithPassphrase(passphrase)
                    if (error == null) {
                        unlockErrorMessage = null
                        onApp()
                    } else {
                        unlockErrorMessage = mapErrorToMessage(error)
                    }
                },
                modifier = Modifier
                    .padding(top = 8.dp)
                    .fillMaxWidth(),
            ) {
                Text("Unlock")
            }
            unlockErrorMessage?.let { message ->
                Text(
                    text = message,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}
