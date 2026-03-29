package com.miguelrodriguez19.safecube.core.vault.data.codec

import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.EncodedSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import kotlinx.serialization.json.Json

internal class NoteSecureItemContentJsonAdapter @Inject constructor() : SecureItemContentJsonAdapter {
    override val itemType: SecureItemType = SecureItemType.NOTE
    override val schemaVersion: Int = NoteSecureItemContent.NOTE_SCHEMA_VERSION

    override fun canEncode(content: SecureItemContent): Boolean = content is NoteSecureItemContent

    override fun encode(
        content: SecureItemContent,
        json: Json,
    ): EncodedSecureItemContent {
        require(content is NoteSecureItemContent) {
            "Note adapter can only encode note content."
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
    ): SecureItemContent = json.decodeFromString<NoteSecureItemContent>(
        payload.toString(StandardCharsets.UTF_8),
    )
}
