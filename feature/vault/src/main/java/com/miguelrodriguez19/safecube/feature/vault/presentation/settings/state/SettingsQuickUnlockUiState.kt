package com.miguelrodriguez19.safecube.feature.vault.presentation.settings.state

import com.miguelrodriguez19.safecube.core.vault.domain.quickunlock.QuickUnlockOfferState

data class SettingsQuickUnlockUiState(
    val offerState: QuickUnlockOfferState = QuickUnlockOfferState.AccountUnavailable,
    val errorMessageRes: Int? = null,
)
