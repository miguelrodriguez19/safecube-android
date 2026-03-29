package com.miguelrodriguez19.safecube.core.vault.data.codec

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.EncodedSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlinx.serialization.json.Json

internal class PasswordSecureItemContentJsonAdapter @Inject constructor() : SecureItemContentJsonAdapter {
    override val itemType: SecureItemType = SecureItemType.PASSWORD
    override val schemaVersion: Int = PasswordSecureItemContent.PASSWORD_SCHEMA_VERSION

    override fun canEncode(content: SecureItemContent): Boolean = content is PasswordSecureItemContent

    override fun encode(
        content: SecureItemContent,
        json: Json,
    ): EncodedSecureItemContent {
        require(content is PasswordSecureItemContent) {
            "Password adapter can only encode password content."
        }

        return EncodedSecureItemContent(
            itemType = content.itemType,
            schemaVersion = content.schemaVersion,
            payload = json.encodeToString(content).toByteArray(StandardCharsets.UTF_8),
        )
    }

    override fun decode(
        payload: ByteArray,
        json: Json,
    ): SecureItemContent = json.decodeFromString<PasswordSecureItemContent>(
        payload.toString(StandardCharsets.UTF_8),
    )
}
