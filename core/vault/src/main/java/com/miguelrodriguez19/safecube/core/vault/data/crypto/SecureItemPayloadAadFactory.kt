package com.miguelrodriguez19.safecube.core.vault.data.crypto

import java.nio.charset.StandardCharsets
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureItemPayloadAadFactory @Inject constructor() {
    fun create(
        accountId: UUID,
        logicalItemId: UUID,
        payloadVersion: Long,
    ): ByteArray = buildString {
        append("accountId:")
        append(accountId)
        append("|logicalItemId:")
        append(logicalItemId)
        append("|payloadVersion:")
        append(payloadVersion)
    }.toByteArray(StandardCharsets.UTF_8)
}
