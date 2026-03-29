package com.miguelrodriguez19.safecube.core.vault.data.codec

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.EncodedSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import kotlinx.serialization.json.Json

internal interface SecureItemContentJsonAdapter {
    val itemType: SecureItemType
    val schemaVersion: Int

    fun canEncode(content: SecureItemContent): Boolean

    fun encode(
        content: SecureItemContent,
        json: Json,
    ): EncodedSecureItemContent

    fun decode(
        payload: ByteArray,
        json: Json,
    ): SecureItemContent
}
