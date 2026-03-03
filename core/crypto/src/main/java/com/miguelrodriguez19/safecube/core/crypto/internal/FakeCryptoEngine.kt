package com.miguelrodriguez19.safecube.core.crypto.internal

import android.util.Log
import com.miguelrodriguez19.safecube.core.crypto.CryptoEngine
import com.miguelrodriguez19.safecube.core.crypto.DecryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.EncryptionRequest
import com.miguelrodriguez19.safecube.core.crypto.EncryptionResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FakeCryptoEngine @Inject constructor() : CryptoEngine {
    override fun encrypt(request: EncryptionRequest): EncryptionResult {
        return EncryptionResult(
            ciphertext = request.plaintext.copyOf(),
            authTag = null,
        )
    }

    override fun decrypt(request: DecryptionRequest): ByteArray {
        Log.i("FakeCryptoEngine", String(request.ciphertext))
        return request.ciphertext.copyOf()
    }
}
