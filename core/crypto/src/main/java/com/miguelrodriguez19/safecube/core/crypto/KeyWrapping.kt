package com.miguelrodriguez19.safecube.core.crypto

interface KeyWrapping {
    fun wrapKey(request: KeyWrapRequest): ByteArray
    fun unwrapKey(request: KeyUnwrapRequest): ByteArray
}
