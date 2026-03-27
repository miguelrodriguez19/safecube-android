package com.miguelrodriguez19.safecube.core.vault.domain.service

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent

sealed interface SecureItemDecryptionResult {
    data class Success(
        val content: SecureItemContent,
    ) : SecureItemDecryptionResult

    data class Error(
        val reason: SecureItemCryptoError,
    ) : SecureItemDecryptionResult
}
