package com.miguelrodriguez19.safecube.core.vault.data.local

import android.content.SharedPreferences
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class VaultKeyMaterialCacheTest {

    @Test
    fun `save and get returns cached key material`() {
        val cache = VaultKeyMaterialCache(InMemorySharedPreferences())
        val expected = createSample()

        cache.save(expected)

        val actual = cache.get()

        requireNotNull(actual)
        assertArrayEquals(expected.kekEncMaster, actual.kekEncMaster)
        assertArrayEquals(expected.kekEncRecovery, actual.kekEncRecovery)
        assertEquals(expected.kdfAlgorithm, actual.kdfAlgorithm)
        assertArrayEquals(expected.kdfSalt, actual.kdfSalt)
        assertEquals(expected.kdfMemoryKib, actual.kdfMemoryKib)
        assertEquals(expected.kdfIterations, actual.kdfIterations)
        assertEquals(expected.kdfParallelism, actual.kdfParallelism)
        assertEquals(expected.kdfOutputLen, actual.kdfOutputLen)
        assertEquals(expected.cryptoVersion, actual.cryptoVersion)
    }

    @Test
    fun `clear removes cached key material`() {
        val cache = VaultKeyMaterialCache(InMemorySharedPreferences())
        cache.save(createSample())

        cache.clear()

        assertNull(cache.get())
    }

    @Test
    fun `get returns null when cache is empty`() {
        val cache = VaultKeyMaterialCache(InMemorySharedPreferences())

        assertNull(cache.get())
    }

    @Test
    fun `get returns null when blob is not valid base64`() {
        val preferences = InMemorySharedPreferences()
        val cache = VaultKeyMaterialCache(preferences)
        cache.save(createSample())
        preferences.edit()
            .putString("kek_enc_master", "###invalid-base64###")
            .apply()

        assertNull(cache.get())
    }

    @Test
    fun `get returns null when second blob is not valid base64`() {
        val preferences = InMemorySharedPreferences()
        val cache = VaultKeyMaterialCache(preferences)
        cache.save(createSample())
        preferences.edit()
            .putString("kek_enc_recovery", "###invalid-base64###")
            .apply()

        assertNull(cache.get())
    }

    @Test
    fun `get returns null when kdf iterations is zero`() {
        val preferences = InMemorySharedPreferences()
        val cache = VaultKeyMaterialCache(preferences)
        cache.save(createSample())
        preferences.edit()
            .putInt("kdf_iterations", 0)
            .apply()

        assertNull(cache.get())
    }

    @Test
    fun `get returns null when crypto version is blank`() {
        val preferences = InMemorySharedPreferences()
        val cache = VaultKeyMaterialCache(preferences)
        cache.save(createSample())
        preferences.edit()
            .putString("crypto_version", " ")
            .apply()

        assertNull(cache.get())
    }

    private fun createSample(): CachedVaultKeyMaterial = CachedVaultKeyMaterial(
        kekEncMaster = byteArrayOf(1, 2, 3),
        kekEncRecovery = byteArrayOf(4, 5, 6),
        kdfAlgorithm = "argon2id",
        kdfSalt = byteArrayOf(7, 8, 9),
        kdfMemoryKib = 65536,
        kdfIterations = 3,
        kdfParallelism = 1,
        kdfOutputLen = 32,
        cryptoVersion = "v1",
    )
}

private class InMemorySharedPreferences : SharedPreferences {
    private val values = LinkedHashMap<String, Any?>()

    override fun getAll(): MutableMap<String, *> = LinkedHashMap(values)

    override fun getString(
        key: String?,
        defValue: String?,
    ): String? = values[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(
        key: String?,
        defValues: MutableSet<String>?,
    ): MutableSet<String>? = (values[key] as? Set<String>)?.toMutableSet() ?: defValues

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
    ) = Unit

    override fun unregisterOnSharedPreferenceChangeListener(
        listener: SharedPreferences.OnSharedPreferenceChangeListener?,
    ) = Unit

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
            if (clearAll) {
                values.clear()
                clearAll = false
            }

            for (key in removedKeys) {
                values.remove(key)
            }
            removedKeys.clear()

            for ((key, value) in updates) {
                if (value == null) {
                    values.remove(key)
                } else {
                    values[key] = value
                }
            }
            updates.clear()
        }
    }
}
