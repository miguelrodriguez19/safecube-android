package com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.crud

sealed interface ObserveSecureItemDetailResult {
    data class Success(
        val detail: SecureItemDetail,
    ) : ObserveSecureItemDetailResult

    data class Error(
        val reason: SecureItemCrudError,
    ) : ObserveSecureItemDetailResult
}
