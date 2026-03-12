package com.miguelrodriguez19.safecube.core.crypto.domain.model

/**
 * Input payload for symmetric encryption.
 *
 * @property plaintext Bytes to encrypt.
 * @property keyMaterial Symmetric key material.
 * @property aad Optional additional authenticated data.
 */
data class EncryptionRequest(
    val plaintext: ByteArray,
    val keyMaterial: ByteArray,
    val aad: ByteArray? = null,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EncryptionRequest

        if (!plaintext.contentEquals(other.plaintext)) return false
        if (!keyMaterial.contentEquals(other.keyMaterial)) return false
        if (!aad.contentEquals(other.aad)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = plaintext.contentHashCode()
        result = 31 * result + keyMaterial.contentHashCode()
        result = 31 * result + (aad?.contentHashCode() ?: 0)
        return result
    }
}
