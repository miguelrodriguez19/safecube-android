package com.miguelrodriguez19.safecube.core.auth.internal

import android.content.SharedPreferences
import androidx.core.content.edit
import com.miguelrodriguez19.safecube.core.auth.TokenStorage
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encrypted adapter for token persistence.
 *
 * Although EncryptedSharedPreferences is deprecated in security-crypto 1.1.0,
 * this remains acceptable for SafeCube v1 MVP and is fully replaceable by
 * swapping TokenStorage binding in DI.
 */
@Singleton
class EncryptedTokenStorage @Inject constructor(
    @param:EncryptedTokenPrefs private val encryptedPreferences: SharedPreferences,
) : TokenStorage {

    private companion object {
        const val KEY_ACCESS_TOKEN = "access_token"
        const val KEY_REFRESH_TOKEN = "refresh_token"
        const val KEY_ISSUED_AT = "issued_at"
    }

    override fun saveTokens(
        accessToken: String,
        refreshToken: String,
        issuedAt: OffsetDateTime?,
    ) {
        encryptedPreferences.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putString(KEY_ISSUED_AT, issuedAt?.toString())
        }
    }

    override fun getAccessToken(): String? = encryptedPreferences.getString(KEY_ACCESS_TOKEN, null)

    override fun getRefreshToken(): String? =
        encryptedPreferences.getString(KEY_REFRESH_TOKEN, null)

    override fun getIssuedAt(): OffsetDateTime? = encryptedPreferences
        .getString(KEY_ISSUED_AT, null)
        ?.let { storedIssuedAt ->
            runCatching { OffsetDateTime.parse(storedIssuedAt) }.getOrNull()
        }

    override fun clear() {
        encryptedPreferences.edit {
            clear()
        }
    }
}
