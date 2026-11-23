package com.miguelrodriguez19.safecube.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing

/** Displays a recovery key with an explicit, temporary reveal action. */
@Composable
fun SafeCubeRecoveryKey(
    value: String,
    modifier: Modifier = Modifier,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    var revealRequested by remember(value) { mutableStateOf(false) }
    var lifecycleState by remember(lifecycleOwner) {
        mutableStateOf(lifecycleOwner.lifecycle.currentState)
    }
    val isHostResumed = lifecycleState.isAtLeast(Lifecycle.State.RESUMED)
    val isRevealed = value.isNotBlank() && revealRequested && isHostResumed
    val title = stringResource(R.string.recovery_key_m142_title)
    val showLabel = stringResource(R.string.recovery_key_m142_show)
    val hideLabel = stringResource(R.string.recovery_key_m142_hide)
    val visibleState = stringResource(R.string.recovery_key_m142_visible)
    val hiddenState = stringResource(R.string.recovery_key_m142_hidden)

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            lifecycleState = lifecycleOwner.lifecycle.currentState
            if (event == Lifecycle.Event.ON_PAUSE || !lifecycleState.isAtLeast(Lifecycle.State.RESUMED)) {
                revealRequested = false
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            revealRequested = false
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    SafeCubeCard(modifier = modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
            )
            Text(
                text = if (isRevealed) value else MASKED_RECOVERY_KEY_PLACEHOLDER,
                modifier = Modifier.semantics {
                    stateDescription = if (isRevealed) visibleState else hiddenState
                },
                style = MaterialTheme.typography.bodyLarge,
            )
            SafeCubeButton(
                text = if (isRevealed) hideLabel else showLabel,
                onClick = {
                    if (isHostResumed && value.isNotBlank()) {
                        revealRequested = !revealRequested
                    }
                },
                variant = SafeCubeButtonVariant.Text,
                enabled = isHostResumed && value.isNotBlank(),
                modifier = Modifier
                    .align(Alignment.End)
                    .padding(top = SafeCubeSpacing.ExtraSmall)
                    .semantics {
                        stateDescription = if (isRevealed) visibleState else hiddenState
                    },
            )
        }
    }
}

private const val MASKED_RECOVERY_KEY_PLACEHOLDER = "•••• •••• ••••"
