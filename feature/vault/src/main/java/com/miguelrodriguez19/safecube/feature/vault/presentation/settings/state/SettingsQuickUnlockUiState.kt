package com.miguelrodriguez19.safecube.feature.vault.presentation.settings.state

import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState
import com.miguelrodriguez19.safecube.feature.vault.presentation.quickunlock.QuickUnlockPromptRequest

data class SettingsQuickUnlockUiState(
    val offerState: QuickUnlockOfferState = QuickUnlockOfferState.AccountUnavailable,
    val errorMessageRes: Int? = null,
    val pendingPrompt: QuickUnlockPromptRequest? = null,
    val promptPresented: Boolean = false,
)
