package com.miguelrodriguez19.safecube.core.auth.data.local

import android.content.SharedPreferences
import java.time.OffsetDateTime
import java.util.concurrent.CopyOnWriteArraySet
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class EncryptedTokenStorageTest {
    @Test
    fun `saveTokens persists access refresh and issuedAt`() {
        val storage = EncryptedTokenStorage(InMemorySharedPreferences())
        val issuedAt = OffsetDateTime.parse("2026-03-05T12:34:56Z")

        storage.saveTokens(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            issuedAt = issuedAt,
        )

        assertEquals("access-token", storage.getAccessToken())
        assertEquals("refresh-token", storage.getRefreshToken())
        assertEquals(issuedAt, storage.getIssuedAt())
    }

    @Test
    fun `saveTokens allows null issuedAt`() {
        val storage = EncryptedTokenStorage(InMemorySharedPreferences())

        storage.saveTokens(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            issuedAt = null,
        )

        assertEquals("access-token", storage.getAccessToken())
        assertEquals("refresh-token", storage.getRefreshToken())
        assertNull(storage.getIssuedAt())
    }

    @Test
    fun `getIssuedAt returns null when stored value is invalid`() {
        val preferences = InMemorySharedPreferences()
        preferences.edit()
            .putString("issued_at", "invalid-date")
            .apply()
        val storage = EncryptedTokenStorage(preferences)

        assertNull(storage.getIssuedAt())
    }

    @Test
    fun `clear removes access refresh and issuedAt`() {
        val storage = EncryptedTokenStorage(InMemorySharedPreferences())
        storage.saveTokens(
            accessToken = "access-token",
            refreshToken = "refresh-token",
            issuedAt = OffsetDateTime.parse("2026-03-05T12:34:56Z"),
        )

        storage.clear()

        assertNull(storage.getAccessToken())
        assertNull(storage.getRefreshToken())
        assertNull(storage.getIssuedAt())
    }
}

private class InMemorySharedPreferences : SharedPreferences {
    private val values = LinkedHashMap<String, Any?>()
    private val listeners = CopyOnWriteArraySet<SharedPreferences.OnSharedPreferenceChangeListener>()

    override fun getAll(): MutableMap<String, *> = LinkedHashMap(values)

    override fun getString(
        key: String?,
        defValue: String?,
    ): String? = values[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(
        key: String?,
        defValues: MutableSet<String>?,
    ): MutableSet<String>? {
        val stored = values[key] as? Set<String> ?: return defValues
        return stored.toMutableSet()
    }

    override fun getInt(
        key: String?,
        defValue: Int,
    ): Int = values[key] as? Int ?: defValue

    override fun getLong(
        key: String?,
        defValue: Long,
    ): Long = values[key] as? Long ?: defValue

    override fun getFloat(
        key: String?,
        defValue: Float,
    ): Float = values[key] as? Float ?: defValue

    override fun getBoolean(
        key: String?,
        defValue: Boolean,
    ): Boolean = values[key] as? Boolean ?: defValue

    override fun contains(key: String?): Boolean = key != null && values.containsKey(key)

    override fun edit(): SharedPreferences.Editor = EditorImpl()

    override fun registerOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) {
        listener ?: return
        listeners.add(listener)
    }

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) {
        listener ?: return
        listeners.remove(listener)
    }

    private inner class EditorImpl : SharedPreferences.Editor {
        private var clearAll = false
        private val updates = LinkedHashMap<String, Any?>()
        private val removedKeys = LinkedHashSet<String>()

        override fun putString(
            key: String?,
            value: String?,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun putStringSet(
            key: String?,
            values: MutableSet<String>?,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = values?.toSet()
            removedKeys.remove(key)
        }

        override fun putInt(
            key: String?,
            value: Int,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun putLong(
            key: String?,
            value: Long,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun putFloat(
            key: String?,
            value: Float,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun putBoolean(
            key: String?,
            value: Boolean,
        ): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            updates[key] = value
            removedKeys.remove(key)
        }

        override fun remove(key: String?): SharedPreferences.Editor = apply {
            if (key == null) return@apply
            removedKeys.add(key)
            updates.remove(key)
        }

        override fun clear(): SharedPreferences.Editor = apply {
            clearAll = true
            updates.clear()
            removedKeys.clear()
        }

        override fun commit(): Boolean {
            applyChanges()
            return true
        }

        override fun apply() {
            applyChanges()
        }

        private fun applyChanges() {
            val changedKeys = LinkedHashSet<String>()

            if (clearAll) {
                changedKeys.addAll(values.keys)
                values.clear()
                clearAll = false
            }

            for (key in removedKeys) {
                if (values.remove(key) != null) {
                    changedKeys.add(key)
                }
            }
            removedKeys.clear()

            for ((key, value) in updates) {
                if (value == null) {
                    if (values.remove(key) != null) {
                        changedKeys.add(key)
                    }
                } else {
                    values[key] = value
                    changedKeys.add(key)
                }
            }
            updates.clear()

            if (changedKeys.isNotEmpty()) {
                listeners.forEach { listener ->
                    changedKeys.forEach { key ->
                        listener.onSharedPreferenceChanged(this@InMemorySharedPreferences, key)
                    }
                }
            }
        }
    }
}
