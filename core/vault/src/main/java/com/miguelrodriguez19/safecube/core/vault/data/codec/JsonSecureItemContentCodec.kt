package com.miguelrodriguez19.safecube.core.vault.data.codec

import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentCodec
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeError
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.EncodedSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.NoteSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.PasswordSecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.nio.charset.StandardCharsets
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonSecureItemContentCodec @Inject constructor() : SecureItemContentCodec {
    private companion object {
        val json = Json {
            prettyPrint = false
            encodeDefaults = false
            explicitNulls = false
            ignoreUnknownKeys = false
            isLenient = false
        }
    }

    override fun encode(content: SecureItemContent): EncodedSecureItemContent = when (content) {
        is PasswordSecureItemContent -> EncodedSecureItemContent(
            itemType = content.itemType,
            schemaVersion = content.schemaVersion,
            payload = json.encodeToString(content).toByteArray(StandardCharsets.UTF_8),
        )

        is NoteSecureItemContent -> EncodedSecureItemContent(
            itemType = content.itemType,
            schemaVersion = content.schemaVersion,
            payload = json.encodeToString(content).toByteArray(StandardCharsets.UTF_8),
        )
    }

    override fun decode(
        itemType: String,
        schemaVersion: Int,
        payload: ByteArray,
    ): SecureItemContentDecodeResult {
        val secureItemType = SecureItemType.fromWireName(itemType)
            ?: return SecureItemContentDecodeResult.Error(
                SecureItemContentDecodeError.UnsupportedItemType(itemType),
            )

        return try {
            when (secureItemType) {
                SecureItemType.PASSWORD -> decodePassword(schemaVersion, payload)
                SecureItemType.NOTE -> decodeNote(schemaVersion, payload)
            }
        } catch (exception: SerializationException) {
            invalidPayload(exception.message ?: "Payload is not valid JSON.")
        } catch (exception: IllegalArgumentException) {
            invalidPayload(exception.message ?: "Payload failed semantic validation.")
        }
    }

    private fun decodePassword(
        schemaVersion: Int,
        payload: ByteArray,
    ): SecureItemContentDecodeResult {
        if (schemaVersion != PasswordSecureItemContent.PASSWORD_SCHEMA_VERSION) {
            return unsupportedSchemaVersion(SecureItemType.PASSWORD.wireName, schemaVersion)
        }

        return SecureItemContentDecodeResult.Success(
            json.decodeFromString<PasswordSecureItemContent>(payload.toString(StandardCharsets.UTF_8)),
        )
    }

    private fun decodeNote(
        schemaVersion: Int,
        payload: ByteArray,
    ): SecureItemContentDecodeResult {
        if (schemaVersion != NoteSecureItemContent.NOTE_SCHEMA_VERSION) {
            return unsupportedSchemaVersion(SecureItemType.NOTE.wireName, schemaVersion)
        }

        return SecureItemContentDecodeResult.Success(
            json.decodeFromString<NoteSecureItemContent>(payload.toString(StandardCharsets.UTF_8)),
        )
    }

    private fun invalidPayload(message: String): SecureItemContentDecodeResult =
        SecureItemContentDecodeResult.Error(
            SecureItemContentDecodeError.InvalidPayload(message),
        )

    private fun unsupportedSchemaVersion(
        itemType: String,
        schemaVersion: Int,
    ): SecureItemContentDecodeResult = SecureItemContentDecodeResult.Error(
        SecureItemContentDecodeError.UnsupportedSchemaVersion(
            itemType = itemType,
            schemaVersion = schemaVersion,
        ),
    )
}
