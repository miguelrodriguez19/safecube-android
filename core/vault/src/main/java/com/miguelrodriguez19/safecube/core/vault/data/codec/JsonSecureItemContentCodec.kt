package com.miguelrodriguez19.safecube.core.vault.data.codec

import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentCodec
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeError
import com.miguelrodriguez19.safecube.core.vault.domain.codec.SecureItemContentDecodeResult
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.SecureItemType
import com.miguelrodriguez19.safecube.core.vault.domain.model.secureitem.itemcontent.SecureItemContent
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class JsonSecureItemContentCodec @Inject internal constructor(
    passwordSecureItemContentJsonAdapter: PasswordSecureItemContentJsonAdapter,
    noteSecureItemContentJsonAdapter: NoteSecureItemContentJsonAdapter,
) : SecureItemContentCodec {
    private companion object {
        val json = Json {
            prettyPrint = false
            encodeDefaults = false
            explicitNulls = false
            ignoreUnknownKeys = false
            isLenient = false
        }
    }

    private val adapters = listOf(
        passwordSecureItemContentJsonAdapter,
        noteSecureItemContentJsonAdapter,
    )

    override fun encode(content: SecureItemContent) = adapters
        .firstOrNull { it.canEncode(content) }
        ?.encode(content, json)
        ?: error("Unsupported secure item content for itemType=${content.itemType.wireName}.")

    override fun decode(
        itemType: String,
        schemaVersion: Int,
        payload: ByteArray,
    ): SecureItemContentDecodeResult {
        val secureItemType = SecureItemType.fromWireName(itemType)
            ?: return SecureItemContentDecodeResult.Error(
                SecureItemContentDecodeError.UnsupportedItemType(itemType),
            )

        val adapter = adapters.firstOrNull {
            it.itemType == secureItemType && it.schemaVersion == schemaVersion
        } ?: return unsupportedSchemaVersion(secureItemType.wireName, schemaVersion)

        return try {
            SecureItemContentDecodeResult.Success(adapter.decode(payload, json))
        } catch (exception: SerializationException) {
            invalidPayload()
        } catch (exception: IllegalArgumentException) {
            invalidPayload()
        }
    }

    private fun invalidPayload(): SecureItemContentDecodeResult =
        SecureItemContentDecodeResult.Error(
            SecureItemContentDecodeError.InvalidPayload,
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
