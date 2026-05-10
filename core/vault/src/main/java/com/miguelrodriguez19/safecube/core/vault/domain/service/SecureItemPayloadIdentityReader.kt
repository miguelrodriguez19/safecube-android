package com.miguelrodriguez19.safecube.core.vault.domain.service

import java.util.UUID

interface SecureItemPayloadIdentityReader {
    fun readLogicalItemId(payload: ByteArray): UUID?
}
