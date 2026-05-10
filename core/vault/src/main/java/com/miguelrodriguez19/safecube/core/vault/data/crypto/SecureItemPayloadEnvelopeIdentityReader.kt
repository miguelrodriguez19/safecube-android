package com.miguelrodriguez19.safecube.core.vault.data.crypto

import com.miguelrodriguez19.safecube.core.vault.domain.service.SecureItemPayloadIdentityReader
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SecureItemPayloadEnvelopeIdentityReader @Inject constructor(
    private val secureItemPayloadEnvelopeV1Codec: SecureItemPayloadEnvelopeV1Codec,
) : SecureItemPayloadIdentityReader {
    override fun readLogicalItemId(payload: ByteArray): UUID? = runCatching {
        secureItemPayloadEnvelopeV1Codec.decode(payload).logicalItemId
    }.getOrNull()
}
