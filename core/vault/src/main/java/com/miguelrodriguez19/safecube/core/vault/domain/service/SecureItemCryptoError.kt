package com.miguelrodriguez19.safecube.core.vault.domain.service

import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeError

sealed interface SecureItemCryptoError {
    data object VaultLocked : SecureItemCryptoError

    data object AccountIdUnavailable : SecureItemCryptoError

    data object MalformedPayload : SecureItemCryptoError

    data object CryptographicFailure : SecureItemCryptoError

    data class ContentDecodingFailed(
        val reason: SecureItemContentDecodeError,
    ) : SecureItemCryptoError
}
