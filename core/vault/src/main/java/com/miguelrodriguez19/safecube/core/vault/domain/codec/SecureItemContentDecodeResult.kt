package com.miguelrodriguez19.safecube.core.vault.domain.codec

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent

sealed interface SecureItemContentDecodeResult {
    data class Success(
        val content: SecureItemContent,
    ) : SecureItemContentDecodeResult

    data class Error(
        val reason: SecureItemContentDecodeError,
    ) : SecureItemContentDecodeResult
}
