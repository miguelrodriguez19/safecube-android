package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.error

import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PrepareSecureItemDraftForSyncError

internal fun PrepareSecureItemDraftForSyncError.asUiMessage(): String = when (this) {
    is PrepareSecureItemDraftForSyncError.DraftNotFound -> "Draft not found."
    is PrepareSecureItemDraftForSyncError.DraftNotPublishable -> "Draft cannot be prepared for sync."
    is PrepareSecureItemDraftForSyncError.LocalStateUpdateFailed -> "Could not prepare draft for sync."
}

internal fun DiscardSecureItemDraftError.asUiMessage(): String = when (this) {
    is DiscardSecureItemDraftError.DraftNotFound -> "Draft not found."
    is DiscardSecureItemDraftError.LocalStateUpdateFailed -> "Could not discard draft."
}
