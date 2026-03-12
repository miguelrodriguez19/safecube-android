package com.miguelrodriguez19.safecube.core.crypto.domain.model

/**
 * Input payload for key wrapping.
 *
 * @property keyToWrap Raw key bytes to protect.
 * @property wrappingKey Key used to wrap [keyToWrap].
 * @property aad Optional additional authenticated data.
 */
data class KeyWrapRequest(
    val keyToWrap: ByteArray,
    val wrappingKey: ByteArray,
    val aad: ByteArray? = null,
)
