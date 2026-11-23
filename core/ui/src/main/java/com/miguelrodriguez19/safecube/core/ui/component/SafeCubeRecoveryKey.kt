package com.miguelrodriguez19.safecube.core.ui.component

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Key
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.ExpandLess
import androidx.compose.material.icons.rounded.ExpandMore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.semantics
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.miguelrodriguez19.safecube.core.ui.R
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceMode
import com.miguelrodriguez19.safecube.core.ui.theme.AppearanceSettings
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeTheme

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
    val largeText = LocalDensity.current.fontScale > 1.3f
    val toggleLabel = if (isRevealed) hideLabel else showLabel
    val toggleIcon = if (isRevealed) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore
    val controls: @Composable () -> Unit = {
        Text(
            toggleLabel,
            color = MaterialTheme.colorScheme.primary,
            style = MaterialTheme.typography.labelLarge
        )
        Icon(toggleIcon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
    }

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

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        color = MaterialTheme.colorScheme.surface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(SafeCubeSpacing.Medium),
            verticalArrangement = Arrangement.spacedBy(SafeCubeSpacing.Small),
        ) {
            Column(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.small)
                    .fillMaxWidth()
                    .heightIn(min = SafeCubeSpacing.TouchTarget)
                    .clickable(
                        enabled = isHostResumed && value.isNotBlank(),
                        role = Role.Button,
                        onClickLabel = toggleLabel,
                    ) {
                        if (isHostResumed && value.isNotBlank()) revealRequested = !revealRequested
                    }
                    .semantics {
                        stateDescription = if (isRevealed) visibleState else hiddenState
                    },
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.padding(SafeCubeSpacing.ExtraSmall),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Rounded.Key,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier
                            .weight(1f)
                            .padding(horizontal = SafeCubeSpacing.Small),
                    )
                    if (!largeText) controls()
                }
                if (largeText) {
                    Row(
                        modifier = Modifier.align(Alignment.End),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        controls()
                    }
                }
            }
            if (isRevealed) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surfaceContainer,
                ) {
                    Box(
                        modifier = modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = value,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontFamily = FontFamily.Monospace,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }

        }
    }
}
