package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.isActive

@Composable
internal fun SyncIconButton(
    isSyncing: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
) {
    val rotation = remember { Animatable(0f) }

    LaunchedEffect(isSyncing) {
        if (!isSyncing) {
            rotation.snapTo(0f)
            return@LaunchedEffect
        }

        while (isActive) {
            rotation.snapTo(0f)
            rotation.animateTo(
                targetValue = 360f,
                animationSpec = tween(
                    durationMillis = 900,
                    easing = LinearEasing,
                ),
            )
        }
    }

    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Default.Sync,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    rotationZ = rotation.value
                },
        )
    }
}
