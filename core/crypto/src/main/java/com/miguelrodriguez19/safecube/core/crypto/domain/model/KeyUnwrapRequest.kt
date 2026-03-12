package com.miguelrodriguez19.safecube.core.crypto.domain.model

/**
 * Input payload for key unwrapping.
 *
 * @property wrappedKey Opaque wrapped key payload.
 * @property wrappingKey Key used to unwrap [wrappedKey].
 * @property aad Optional additional authenticated data.
 */
data class KeyUnwrapRequest(
    val wrappedKey: ByteArray,
    val wrappingKey: ByteArray,
    val aad: ByteArray? = null,
)
