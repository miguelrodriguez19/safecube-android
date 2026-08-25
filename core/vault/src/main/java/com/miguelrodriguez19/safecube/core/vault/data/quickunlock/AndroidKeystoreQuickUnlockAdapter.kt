package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.UserNotAuthenticatedException
import java.security.UnrecoverableKeyException
import java.util.UUID
import javax.crypto.AEADBadTagException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidKeystoreQuickUnlockAdapter @Inject constructor(
    private val platform: QuickUnlockAndroidKeyStorePlatform,
    private val envelopeCodec: QuickUnlockEnvelopeCodec,
) : QuickUnlockKeyStore {
    private val pendingCiphers = mutableMapOf<String, PendingCipher>()

    @Synchronized
    override fun isSupported(): Boolean {
        return platform.isSupported()
    }

    @Synchronized
    override fun hasAlias(accountId: UUID): Boolean = runCatching {
        platform.hasAlias(accountId)
    }.getOrDefault(false)

    @Synchronized
    override fun prepareWrap(
        accountId: UUID,
        operationId: String,
    ): QuickUnlockKeyStorePrepareResult {
        if (!isSupported()) return QuickUnlockKeyStorePrepareResult.Unsupported
        return try {
            if (platform.hasAlias(accountId)) return QuickUnlockKeyStorePrepareResult.InvalidEnrollment
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, platform.createKey(accountId))
            pendingCiphers[operationId] = PendingCipher(
                cipher = cipher,
                aad = QuickUnlockAliasFactory.aadFor(accountId),
            )
            QuickUnlockKeyStorePrepareResult.Ready
        } catch (_: Throwable) {
            platform.delete(accountId)
            QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable
        }
    }

    @Synchronized
    override fun finishWrap(
        operationId: String,
        kek: ByteArray,
    ): QuickUnlockKeyStoreWrapResult {
        val pending = pendingCiphers.remove(operationId) ?: return QuickUnlockKeyStoreWrapResult.Failed
        return try {
            pending.cipher.updateAAD(pending.aad)
            envelopeCodec.encode(pending.cipher.iv, pending.cipher.doFinal(kek))
                ?.let(QuickUnlockKeyStoreWrapResult::Success)
                ?: QuickUnlockKeyStoreWrapResult.Failed
        } catch (_: UserNotAuthenticatedException) {
            QuickUnlockKeyStoreWrapResult.Failed
        } catch (_: Throwable) {
            QuickUnlockKeyStoreWrapResult.Failed
        }
    }

    @Synchronized
    override fun prepareUnwrap(
        accountId: UUID,
        envelope: ByteArray,
        operationId: String,
    ): QuickUnlockKeyStorePrepareResult {
        if (!isSupported()) return QuickUnlockKeyStorePrepareResult.Unsupported
        val decoded = envelopeCodec.decode(envelope)
        if (decoded !is QuickUnlockEnvelopeDecodeResult.Valid) {
            return QuickUnlockKeyStorePrepareResult.InvalidEnrollment
        }
        return try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(
                Cipher.DECRYPT_MODE,
                platform.loadKey(accountId)
                    ?: throw UnrecoverableKeyException(QuickUnlockAliasFactory.aliasFor(accountId)),
                GCMParameterSpec(TAG_LENGTH_BITS, decoded.nonce),
            )
            pendingCiphers[operationId] = PendingCipher(
                cipher = cipher,
                aad = QuickUnlockAliasFactory.aadFor(accountId),
                ciphertextAndTag = decoded.ciphertextAndTag,
            )
            QuickUnlockKeyStorePrepareResult.Ready
        } catch (_: KeyPermanentlyInvalidatedException) {
            QuickUnlockKeyStorePrepareResult.InvalidEnrollment
        } catch (_: UnrecoverableKeyException) {
            QuickUnlockKeyStorePrepareResult.InvalidEnrollment
        } catch (_: java.security.KeyStoreException) {
            QuickUnlockKeyStorePrepareResult.InvalidEnrollment
        } catch (_: Throwable) {
            QuickUnlockKeyStorePrepareResult.TemporarilyUnavailable
        }
    }

    @Synchronized
    override fun finishUnwrap(operationId: String): QuickUnlockKeyStoreFinishResult {
        val pending = pendingCiphers.remove(operationId)
            ?: return QuickUnlockKeyStoreFinishResult.AuthenticationFailed
        return try {
            val ciphertextAndTag = pending.ciphertextAndTag
                ?: return QuickUnlockKeyStoreFinishResult.InvalidEnrollment
            pending.cipher.updateAAD(pending.aad)
            val plaintext = pending.cipher.doFinal(ciphertextAndTag)
            if (plaintext.size == KEK_LENGTH) {
                QuickUnlockKeyStoreFinishResult.Success(plaintext)
            } else {
                plaintext.fill(0)
                QuickUnlockKeyStoreFinishResult.InvalidEnrollment
            }
        } catch (_: UserNotAuthenticatedException) {
            QuickUnlockKeyStoreFinishResult.AuthenticationFailed
        } catch (_: AEADBadTagException) {
            QuickUnlockKeyStoreFinishResult.InvalidEnrollment
        } catch (_: BadPaddingException) {
            QuickUnlockKeyStoreFinishResult.InvalidEnrollment
        } catch (_: KeyPermanentlyInvalidatedException) {
            QuickUnlockKeyStoreFinishResult.InvalidEnrollment
        } catch (_: Throwable) {
            QuickUnlockKeyStoreFinishResult.TemporarilyUnavailable
        }
    }

    @Synchronized
    override fun cipherFor(operationId: String): Cipher? = pendingCiphers[operationId]?.cipher

    @Synchronized
    override fun acceptAuthenticatedCipher(operationId: String, cipher: Cipher?): Boolean {
        val pending = pendingCiphers[operationId] ?: return false
        if (cipher == null) return false
        pendingCiphers[operationId] = pending.copy(cipher = cipher)
        return true
    }

    @Synchronized
    override fun cancel(operationId: String) {
        pendingCiphers.remove(operationId)
    }

    @Synchronized
    override fun delete(accountId: UUID): Boolean = platform.delete(accountId)

    @Synchronized
    override fun deleteAll(): Boolean = platform.deleteAll()

    private data class PendingCipher(
        val cipher: Cipher,
        val aad: ByteArray,
        val ciphertextAndTag: ByteArray? = null,
    )

    private companion object {
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEK_LENGTH = 32
        const val TAG_LENGTH_BITS = 128
    }
}
