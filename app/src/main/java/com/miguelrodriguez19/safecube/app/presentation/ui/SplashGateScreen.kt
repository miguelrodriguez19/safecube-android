package com.miguelrodriguez19.safecube.app.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.miguelrodriguez19.safecube.R
import com.miguelrodriguez19.safecube.core.ui.R as UiR
import com.miguelrodriguez19.safecube.core.ui.component.SafeCubeProgress
import com.miguelrodriguez19.safecube.core.ui.theme.SafeCubeSpacing

private val SPLASH_MARK_SIZE = 96.dp

@Composable
fun SplashGateScreen() {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .padding(SafeCubeSpacing.ExtraLarge),
            verticalArrangement = Arrangement.spacedBy(
                SafeCubeSpacing.ExtraLarge,
                Alignment.CenterVertically,
            ),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(
                painter = painterResource(UiR.drawable.ic_safecube_mark),
                contentDescription = null,
                modifier = Modifier.size(SPLASH_MARK_SIZE),
                tint = MaterialTheme.colorScheme.primary,
            )
            SafeCubeProgress(message = stringResource(R.string.shell_loading_session))
        }
    }
}
