package com.miguelrodriguez19.safecube.core.vault.domain.service

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItem
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import java.util.UUID

interface SecureItemCryptoService {
    fun encrypt(
        logicalItemId: UUID,
        payloadVersion: Long,
        content: SecureItemContent,
    ): SecureItemEncryptionResult

    fun decrypt(item: SecureItem): SecureItemDecryptionResult
}
