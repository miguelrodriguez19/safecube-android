package com.miguelrodriguez19.safecube.feature.vault.presentation.shared.error

import com.miguelrodriguez19.safecube.core.vault.domain.model.remote.result.SecureItemRemoteError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.DiscardSecureItemDraftError
import com.miguelrodriguez19.safecube.core.vault.domain.model.sync.draft.PublishSecureItemDraftError

internal fun PublishSecureItemDraftError.asUiMessage(): String = when (this) {
    is PublishSecureItemDraftError.DraftNotFound -> "Draft not found."
    is PublishSecureItemDraftError.MissingRemoteItemId -> "Draft cannot be published because remote id is missing."
    is PublishSecureItemDraftError.RemoteOperationFailed -> error.asUiMessage()
    is PublishSecureItemDraftError.LocalStateUpdateFailed -> "Could not update local state after publishing draft."
}

internal fun DiscardSecureItemDraftError.asUiMessage(): String = when (this) {
    is DiscardSecureItemDraftError.DraftNotFound -> "Draft not found."
    is DiscardSecureItemDraftError.LocalStateUpdateFailed -> "Could not discard draft."
}

private fun SecureItemRemoteError.asUiMessage(): String = when (this) {
    SecureItemRemoteError.Unauthorized -> "Session is not valid. Please unlock vault again."
    SecureItemRemoteError.ItemNotFound -> "Item was deleted on backend. Automatic restore is not available."
    SecureItemRemoteError.Conflict -> "Draft publish conflicted with backend state."
    is SecureItemRemoteError.HttpError -> "Remote request failed with HTTP ${statusCode}."
    is SecureItemRemoteError.NetworkError -> "Network error while publishing draft."
}
