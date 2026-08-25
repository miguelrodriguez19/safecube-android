package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import android.app.KeyguardManager
import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.biometric.BiometricManager
import dagger.hilt.android.qualifiers.ApplicationContext
import java.security.KeyStore
import java.util.UUID
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class AndroidQuickUnlockKeyStorePlatformImpl @Inject constructor(
    @ApplicationContext private val context: Context,
) : QuickUnlockAndroidKeyStorePlatform {
    override fun isSupported(): Boolean {
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
            BiometricManager.Authenticators.DEVICE_CREDENTIAL
        return context.getSystemService(KeyguardManager::class.java)?.isDeviceSecure == true &&
            BiometricManager.from(context).canAuthenticate(authenticators) == BiometricManager.BIOMETRIC_SUCCESS
    }

    override fun createKey(accountId: UUID): SecretKey {
        val authenticators = KeyProperties.AUTH_BIOMETRIC_STRONG or KeyProperties.AUTH_DEVICE_CREDENTIAL
        return KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE).apply {
            init(
                KeyGenParameterSpec.Builder(
                    QuickUnlockAliasFactory.aliasFor(accountId),
                    KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
                )
                    .setKeySize(KEK_LENGTH_BITS)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setUserAuthenticationRequired(true)
                    .setUserAuthenticationParameters(0, authenticators)
                    .build(),
            )
        }.generateKey()
    }

    override fun loadKey(accountId: UUID): SecretKey? =
        keyStore().getKey(QuickUnlockAliasFactory.aliasFor(accountId), null) as? SecretKey

    override fun hasAlias(accountId: UUID): Boolean = runCatching {
        keyStore().containsAlias(QuickUnlockAliasFactory.aliasFor(accountId))
    }.getOrDefault(false)

    override fun delete(accountId: UUID): Boolean = runCatching {
        val keyStore = keyStore()
        val alias = QuickUnlockAliasFactory.aliasFor(accountId)
        if (keyStore.containsAlias(alias)) keyStore.deleteEntry(alias)
        true
    }.getOrDefault(false)

    override fun deleteAll(): Boolean = runCatching {
        val keyStore = keyStore()
        val aliases = keyStore.aliases()
        while (aliases.hasMoreElements()) {
            aliases.nextElement().takeIf { it.startsWith(QuickUnlockAliasFactory.ALIAS_PREFIX) }
                ?.let(keyStore::deleteEntry)
        }
        true
    }.getOrDefault(false)

    private fun keyStore(): KeyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }

    private companion object {
        const val ANDROID_KEYSTORE = "AndroidKeyStore"
        const val KEK_LENGTH_BITS = 256
    }
}
