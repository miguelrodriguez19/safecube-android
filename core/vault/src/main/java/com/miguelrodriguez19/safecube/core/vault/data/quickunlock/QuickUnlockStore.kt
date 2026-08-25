package com.miguelrodriguez19.safecube.core.vault.data.quickunlock

import android.content.SharedPreferences
import java.util.Base64
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class QuickUnlockStore @Inject constructor(
    @param:QuickUnlockPreferences private val preferences: SharedPreferences,
) {
    fun readEnvelope(accountId: UUID): QuickUnlockStoredEnvelope {
        val raw = preferences.getString(envelopeKey(accountId), null)
            ?: return QuickUnlockStoredEnvelope.Absent
        return runCatching { Base64.getDecoder().decode(raw) }
            .getOrNull()
            ?.takeIf { it.isNotEmpty() }
            ?.let(QuickUnlockStoredEnvelope::Present)
            ?: QuickUnlockStoredEnvelope.Corrupted
    }

    fun saveEnvelope(accountId: UUID, envelope: ByteArray): Boolean {
        val encoded = Base64.getEncoder().encodeToString(envelope)
        return preferences.edit().putString(envelopeKey(accountId), encoded).commit() &&
            preferences.getString(envelopeKey(accountId), null) == encoded
    }

    fun hasSeenOffer(accountId: UUID): Boolean = preferences.getBoolean(offerSeenKey(accountId), false)

    fun markOfferSeen(accountId: UUID): Boolean =
        preferences.edit().putBoolean(offerSeenKey(accountId), true).commit() &&
            preferences.getBoolean(offerSeenKey(accountId), false)

    /** Technical cleanup: preserves the user's offer decision. */
    fun clearEnrollmentArtifact(accountId: UUID): Boolean {
        val committed = preferences.edit()
            .remove(envelopeKey(accountId))
            .commit()
        return committed &&
            preferences.getString(envelopeKey(accountId), null) == null
    }

    /** Account cleanup: removes both the enrollment artifact and the non-sensitive offer marker. */
    fun clearAccount(accountId: UUID): Boolean {
        val committed = preferences.edit()
            .remove(envelopeKey(accountId))
            .remove(offerSeenKey(accountId))
            .commit()
        return committed && preferences.getString(envelopeKey(accountId), null) == null &&
            !preferences.getBoolean(offerSeenKey(accountId), false)
    }

    fun clearAll(): Boolean {
        val keys = preferences.all.keys.filter { it.startsWith(KEY_PREFIX) }
        if (keys.isEmpty()) return true
        val editor = preferences.edit()
        keys.forEach(editor::remove)
        return editor.commit() && preferences.all.keys.none { it.startsWith(KEY_PREFIX) }
    }

    private fun envelopeKey(accountId: UUID): String = "$KEY_PREFIX${QuickUnlockAliasFactory.accountHash(accountId)}.envelope"

    private fun offerSeenKey(accountId: UUID): String = "$KEY_PREFIX${QuickUnlockAliasFactory.accountHash(accountId)}.offer_seen"

    private companion object {
        const val KEY_PREFIX = "quick_unlock."
    }
}

internal sealed interface QuickUnlockStoredEnvelope {
    data class Present(val value: ByteArray) : QuickUnlockStoredEnvelope

    data object Absent : QuickUnlockStoredEnvelope

    data object Corrupted : QuickUnlockStoredEnvelope
}
