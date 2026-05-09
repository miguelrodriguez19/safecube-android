package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

sealed interface ObserveSecureItemDraftDetailResult {
    data class Success(
        val detail: SecureItemDraftDetail,
    ) : ObserveSecureItemDraftDetailResult

    data object NotFound : ObserveSecureItemDraftDetailResult

    data class Error(
        val reason: SecureItemCrudError,
    ) : ObserveSecureItemDraftDetailResult
}
