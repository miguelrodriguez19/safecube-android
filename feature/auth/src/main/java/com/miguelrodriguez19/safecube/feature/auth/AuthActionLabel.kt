package com.miguelrodriguez19.safecube.feature.auth

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.miguelrodriguez19.safecube.core.ui.R as UiR

@Composable
fun AuthActionLabel() {
    Text(text = stringResource(UiR.string.action_save))
}
