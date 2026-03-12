package com.miguelrodriguez19.safecube.core.crypto.domain.port

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KdfRequest

/**
 * Abstraction for key-derivation algorithms.
 */
interface KdfEngine {
    /**
     * Derives a key from the provided [request].
     */
    fun deriveKey(request: KdfRequest): ByteArray
}
