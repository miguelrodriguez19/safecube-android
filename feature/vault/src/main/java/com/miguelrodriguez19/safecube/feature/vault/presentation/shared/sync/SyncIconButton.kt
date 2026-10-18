package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIconSize
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIcons
import kotlinx.coroutines.isActive

@Composable
internal fun SyncIconButton(
    isSyncing: Boolean,
    enabled: Boolean,
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

    IconButton(
        onClick = onClick,
        enabled = enabled,
    ) {
        Icon(
            imageVector = SafeCubeIcons.Sync,
            contentDescription = contentDescription,
            modifier = Modifier
                .size(SafeCubeIconSize.Default)
                .graphicsLayer {
                    rotationZ = rotation.value
                },
        )
    }
}
