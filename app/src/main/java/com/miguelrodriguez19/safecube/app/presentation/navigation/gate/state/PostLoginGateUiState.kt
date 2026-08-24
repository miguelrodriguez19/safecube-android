package com.miguelrodriguez19.safecube.app.presentation.navigation.gate.state

import androidx.annotation.StringRes
import com.miguelrodriguez19.safecube.core.ui.R as UiR

data class PostLoginGateUiState(
    val isLoading: Boolean = true,
    @get:StringRes val messageRes: Int = UiR.string.vault_bootstrap_loading,
)
