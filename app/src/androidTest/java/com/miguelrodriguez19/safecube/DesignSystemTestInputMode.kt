package com.miguelrodriguez19.safecube

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.platform.LocalInputModeManager

/** Request keyboard mode before exercising non-editable controls' focus actions. */
@Composable
internal fun DesignSystemTestKeyboardMode() {
    val inputModeManager = LocalInputModeManager.current
    LaunchedEffect(inputModeManager) {
        inputModeManager.requestInputMode(InputMode.Keyboard)
    }
}
