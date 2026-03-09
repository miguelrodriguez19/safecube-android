package com.miguelrodriguez19.safecube.core.crypto

data class DecryptionRequest(
    val ciphertext: ByteArray,
    val keyMaterial: ByteArray,
    val iv: ByteArray,
    val aad: ByteArray? = null,
    val authTag: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DecryptionRequest

        if (!ciphertext.contentEquals(other.ciphertext)) return false
        if (!keyMaterial.contentEquals(other.keyMaterial)) return false
        if (!iv.contentEquals(other.iv)) return false
        if (!aad.contentEquals(other.aad)) return false
        if (!authTag.contentEquals(other.authTag)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = ciphertext.contentHashCode()
        result = 31 * result + keyMaterial.contentHashCode()
        result = 31 * result + iv.contentHashCode()
        result = 31 * result + (aad?.contentHashCode() ?: 0)
        result = 31 * result + authTag.contentHashCode()
        return result
    }
}
