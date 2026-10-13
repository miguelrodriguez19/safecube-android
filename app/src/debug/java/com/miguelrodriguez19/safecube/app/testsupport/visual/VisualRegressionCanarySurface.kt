package com.miguelrodriguez19.safecube.app.testsupport.visual

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.component.SecretOutlinedTextField

enum class VisualRegressionCanaryAction {
    SubmitAuth,
    RetrySync,
    SyncNow,
}

@Composable
fun VisualRegressionCanarySurface(
    fixture: VisualRegressionCanaryFixture,
    onAction: (VisualRegressionCanaryAction) -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.vault_label),
                style = MaterialTheme.typography.headlineSmall,
            )
            OutlinedTextField(
                value = fixture.auth.email,
                onValueChange = {},
                label = { Text(stringResource(R.string.email_label)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
            )
            SecretOutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text(stringResource(R.string.password_label)) },
                modifier = Modifier.fillMaxWidth(),
            )
            Button(
                onClick = { onAction(VisualRegressionCanaryAction.SubmitAuth) },
                enabled = fixture.auth.submitEnabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringResource(R.string.login))
            }
            OutlinedButton(
                onClick = { onAction(VisualRegressionCanaryAction.SyncNow) },
                enabled = fixture.sync.actionEnabled,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    stringResource(
                        if (fixture.sync.isSyncing) {
                            R.string.sync_status_syncing
                        } else {
                            R.string.sync_now_action
                        },
                    ),
                )
            }
            fixture.vault.items.forEach { item ->
                VisualItemCard(item)
            }
            if (fixture.error.visible) {
                Text(
                    text = stringResource(R.string.sync_error_offline_timeout),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                )
                OutlinedButton(
                    onClick = { onAction(VisualRegressionCanaryAction.RetrySync) },
                    enabled = fixture.error.retryEnabled,
                ) {
                    Text(stringResource(R.string.retry))
                }
            }
        }
    }
}

@Composable
private fun VisualItemCard(item: ItemVisualFixture) {
    val stateLabel = when (item.state) {
        ItemVisualState.Synced -> stringResource(R.string.sync_status_synced)
        ItemVisualState.Draft -> stringResource(R.string.draft_badge_label)
        ItemVisualState.Conflict -> stringResource(R.string.sync_status_conflict)
    }
    val typeLabel = when (item.type) {
        ItemVisualType.Password -> stringResource(R.string.vault_item_type_password)
        ItemVisualType.Note -> stringResource(R.string.vault_item_type_note)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .semantics { stateDescription = stateLabel },
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = item.title,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = typeLabel,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.labelLarge,
            )
            if (item.state != ItemVisualState.Synced) {
                Text(
                    text = stateLabel,
                    color = if (item.state == ItemVisualState.Conflict) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.tertiary
                    },
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
    }
}

