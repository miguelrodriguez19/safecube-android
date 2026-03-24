package com.miguelrodriguez19.safecube.core.vault.domain.codec

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.EncodedSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent

interface SecureItemContentCodec {
    fun encode(content: SecureItemContent): EncodedSecureItemContent

    fun decode(
        itemType: String,
        schemaVersion: Int,
        payload: ByteArray,
    ): SecureItemContentDecodeResult
}
