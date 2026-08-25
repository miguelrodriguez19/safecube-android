package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import android.app.KeyguardManager
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.security.keystore.UserNotAuthenticatedException
import androidx.biometric.BiometricManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import java.security.UnrecoverableKeyException
import java.util.UUID
import javax.crypto.AEADBadTagException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidKeystoreQuickUnlockAdapter @Inject constructor(
    @ApplicationContext private val context: Context,
    private val envelopeCodec: QuickUnlockEnvelopeCodec,
) : QuickUnlockKeyStore {
    private val pendingCiphers = mutableMapOf<String, PendingCipher>()

    @Synchronized
    override fun isSupported(): Boolean {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return context.getSystemService(KeyguardManager::class.java)?.isDeviceSecure == true &&
            BiometricManager.from(context).canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    @Synchronized
    override fun hasAlias(accountId: UUID): Boolean = runCatching {
        keyStore().containsAlias(QuickUnlockAliasFactory.aliasFor(accountId))
    }.getOrDefault(false)

    @Synchronized
    override fun prepareWrap(
        accountId: UUID,
        operationId: String,
    ): QuickUnlockKeyStorePrepareResult {
        if (!isSupported()) return QuickUnlockKeyStorePrepareResult.Unsupported
        val alias = QuickUnlockAliasFactory.aliasFor(accountId)
        return try {
            if (keyStore().containsAlias(alias)) return QuickUnlockKeyStorePrepareResult.InvalidEnrollment
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, createKey(alias))
            cipher.updateAAD(QuickUnlockAliasFactory.aadFor(accountId))
            pendingCiphers[operationId] = PendingCipher(cipher)
            QuickUnlockKeyStorePrepareResult.Ready
        } catch (_: Throwable) {
            deleteAlias(alias)
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
                loadKey(QuickUnlockAliasFactory.aliasFor(accountId)),
                GCMParameterSpec(TAG_LENGTH_BITS, decoded.nonce),
            )
            cipher.updateAAD(QuickUnlockAliasFactory.aadFor(accountId))
            pendingCiphers[operationId] = PendingCipher(cipher, decoded.ciphertextAndTag)
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
    override fun cancel(operationId: String) {
        pendingCiphers.remove(operationId)
    }

    @Synchronized
    override fun delete(accountId: UUID): Boolean = deleteAlias(QuickUnlockAliasFactory.aliasFor(accountId))

    @Synchronized
    override fun deleteAll(): Boolean = runCatching {
        val keyStore = keyStore()
        val aliases = keyStore.aliases()
        val matchingAliases = buildList {
            while (aliases.hasMoreElements()) {
                aliases.nextElement().takeIf {
                    it.startsWith(QuickUnlockAliasFactory.ALIAS_PREFIX)
                }?.let(::add)
            }
        }
        matchingAliases.forEach(keyStore::deleteEntry)
        true
    }.getOrDefault(false)

    private fun createKey(alias: String): SecretKey {
        val authenticatorPolicy = KeyProperties.AUTH_BIOMETRIC_STRONG or
            KeyProperties.AUTH_DEVICE_CREDENTIAL
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).apply {
            init(
                KeyGenParameterSpec.Builder(
                    alias,
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setKeySize(KEK_LENGTH * 8)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationParameters(0, authenticatorPolicy)
                    .build(),
            )
        }.generateKey()
    }

    private fun loadKey(alias: String): SecretKey = keyStore().getKey(alias, null) as? SecretKey
        ?: throw UnrecoverableKeyException(alias)

    private fun deleteAlias(alias: String): Boolean = runCatching {
        val keyStore = keyStore()
        if (keyStore.containsAlias(alias)) keyStore.deleteEntry(alias)
        true
    }.getOrDefault(false)

    private fun keyStore(): KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private data class PendingCipher(
        val cipher: Cipher,
        val ciphertextAndTag: ByteArray? = null,
    )

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val TRANSFORMATION = "AES/GCM/NoPadding"
        const val KEK_LENGTH = 32
        const val TAG_LENGTH_BITS = 128
    }
}
