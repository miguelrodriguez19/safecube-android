package com.miguelrodriguez19.safecube.core.crypto

interface KdfEngine {
    fun deriveKey(request: KdfRequest): ByteArray
}
