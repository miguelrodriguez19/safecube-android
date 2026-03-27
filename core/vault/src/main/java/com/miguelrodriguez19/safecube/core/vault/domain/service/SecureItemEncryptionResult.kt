package com.miguelrodriguez19.safecube.core.vault.domain.service

sealed interface SecureItemEncryptionResult {
    data class Success(
        val payload: EncryptedSecureItemPayload,
    ) : SecureItemEncryptionResult

    data class Error(
        val reason: SecureItemCryptoError,
    ) : SecureItemEncryptionResult
}
