package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.sync

import androidx.compose.runtime.Composable
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeIconButton
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeIcons

@Composable
internal fun SyncIconButton(
    isSyncing: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
) {
    SafeCubeIconButton(
        imageVector = SafeCubeIcons.Sync,
        contentDescription = contentDescription,
        onClick = onClick,
        enabled = enabled,
        loading = isSyncing,
    )
}
