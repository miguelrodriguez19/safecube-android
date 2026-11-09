package com.miguelrodriguez19.safecube.core.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing

private val EmptyTopBar: @Composable () -> Unit = {}

/**
 * Provides the shared SafeCube screen shell without owning navigation or business state.
 *
 * The scaffold owns the background and safe-drawing content insets. The top bar owns its
 * top and horizontal safe-drawing insets when supplied. The canonical bottom navigation
 * owns its default bottom and horizontal safe-drawing insets.
 */
@Composable
fun SafeCubeScaffold(
    modifier: Modifier = Modifier,
    topBar: @Composable () -> Unit = EmptyTopBar,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (topBar !== EmptyTopBar) {
                Box(
                    modifier = Modifier.windowInsetsPadding(
                        WindowInsets.safeDrawing.only(
                            WindowInsetsSides.Top + WindowInsetsSides.Horizontal,
                        ),
                    ),
                ) {
                    topBar()
                }
            }
        },
        bottomBar = bottomBar,
        snackbarHost = snackbarHost,
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .padding(SafeCubeSpacing.Large),
            content = content,
        )
    }
}
