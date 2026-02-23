package com.miguelrodriguez19.safecube.core.auth.internal

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.miguelrodriguez19.safecube.core.auth.TokenStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

/**
 * Uses EncryptedSharedPreferences for storing session tokens.
 *
 * Although deprecated in security-crypto 1.1.0, this is acceptable for SafeCube v1:
 * - Only stores JWT + refresh tokens
 * - Does not store vault cryptographic material
 * - Fully encapsulated behind TokenStorage
 *
 * TODO: replace with AndroidKeyStore-based implementation post MVP.
 */
@Singleton
class EncryptedTokenStorage @Inject constructor(
    @ApplicationContext context: Context,
) : TokenStorage {

    private val encryptedPreferences = EncryptedSharedPreferences.create(
        context,
        PREFERENCES_NAME,
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
    )

    override fun saveAccessToken(token: String) {
        encryptedPreferences.edit {
            putString(KEY_ACCESS_TOKEN, token)
        }
    }

    override fun saveRefreshToken(token: String) {
        encryptedPreferences.edit {
            putString(KEY_REFRESH_TOKEN, token)
        }
    }

    override fun getAccessToken(): String? = encryptedPreferences.getString(KEY_ACCESS_TOKEN, null)

    override fun getRefreshToken(): String? =
        encryptedPreferences.getString(KEY_REFRESH_TOKEN, null)

    override fun clear() {
        encryptedPreferences.edit {
            clear()
        }
    }

    private companion object {
        const val PREFERENCES_NAME = "auth_encrypted_preferences"
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
    }
}
