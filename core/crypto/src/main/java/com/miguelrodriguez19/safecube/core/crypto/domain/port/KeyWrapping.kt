package com.miguelrodriguez19.safecube.core.crypto.domain.port

import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyUnwrapRequest
import com.miguelrodriguez19.safecube.core.crypto.domain.model.KeyWrapRequest

/**
 * Abstraction for key wrapping and unwrapping operations.
 */
interface KeyWrapping {
    /**
     * Wraps a raw key with a wrapping key and returns an opaque wrapped payload.
     */
    fun wrapKey(request: KeyWrapRequest): ByteArray

    /**
     * Unwraps a previously wrapped key.
     */
    fun unwrapKey(request: KeyUnwrapRequest): ByteArray
}
