package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import java.util.UUID
import javax.crypto.Cipher

/** Public bridge for the UI to build its system-prompt CryptoObject without receiving key material. */
interface QuickUnlockPromptCipherProvider {
    fun cipherFor(operationId: String): Cipher?

    /** Records the Cipher returned by BiometricPrompt after a successful authentication callback. */
    fun acceptAuthenticatedCipher(operationId: String, cipher: Cipher?): Boolean
}

internal interface QuickUnlockKeyStore : QuickUnlockPromptCipherProvider {
    fun isSupported(): Boolean

    fun hasAlias(accountId: UUID): Boolean

    fun prepareWrap(
        accountId: UUID,
        operationId: String,
    ): QuickUnlockKeyStorePrepareResult

    fun finishWrap(
        operationId: String,
        kek: ByteArray,
    ): QuickUnlockKeyStoreWrapResult

    fun prepareUnwrap(
        accountId: UUID,
        envelope: ByteArray,
        operationId: String,
    ): QuickUnlockKeyStorePrepareResult

    fun finishUnwrap(operationId: String): QuickUnlockKeyStoreFinishResult

    fun cancel(operationId: String)

    fun delete(accountId: UUID): Boolean

    fun deleteAll(): Boolean
}

internal sealed interface QuickUnlockKeyStoreWrapResult {
    data class Success(val envelope: ByteArray) : QuickUnlockKeyStoreWrapResult

    data object Unsupported : QuickUnlockKeyStoreWrapResult

    data object Failed : QuickUnlockKeyStoreWrapResult
}

internal sealed interface QuickUnlockKeyStorePrepareResult {
    data object Ready : QuickUnlockKeyStorePrepareResult

    data object Unsupported : QuickUnlockKeyStorePrepareResult

    data object TemporarilyUnavailable : QuickUnlockKeyStorePrepareResult

    data object InvalidEnrollment : QuickUnlockKeyStorePrepareResult
}

internal sealed interface QuickUnlockKeyStoreFinishResult {
    data class Success(val kek: ByteArray) : QuickUnlockKeyStoreFinishResult

    data object AuthenticationFailed : QuickUnlockKeyStoreFinishResult

    data object TemporarilyUnavailable : QuickUnlockKeyStoreFinishResult

    data object InvalidEnrollment : QuickUnlockKeyStoreFinishResult
}
