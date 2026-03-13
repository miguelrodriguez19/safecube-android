package com.miguelrodriguez19.safecube.core.network.serialization

import java.util.Base64
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*

object Base64ByteArraySerializer : KSerializer<ByteArray> {

    override val descriptor =
        PrimitiveSerialDescriptor("Base64ByteArray", PrimitiveKind.STRING)

    override fun serialize(
        encoder: Encoder,
        value: ByteArray,
    ) {
        encoder.encodeString(Base64.getEncoder().encodeToString(value))
    }

    override fun deserialize(
        decoder: Decoder,
    ): ByteArray {
        return Base64.getDecoder().decode(decoder.decodeString())
    }
}