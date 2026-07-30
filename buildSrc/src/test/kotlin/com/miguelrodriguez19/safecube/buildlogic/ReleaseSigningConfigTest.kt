package com.miguelrodriguez19.safecube.buildlogic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class ReleaseSigningConfigTest {
    @Test
    fun `resolves complete release signing configuration`() {
        val credentials = ReleaseSigningConfig.resolve(completeEnvironment())

        requireNotNull(credentials)
        assertEquals("/tmp/release.jks", credentials.keystorePath)
        assertEquals("store-password", credentials.storePassword)
        assertEquals("release-key", credentials.keyAlias)
        assertEquals("key-password", credentials.keyPassword)
    }

    @Test
    fun `returns no configuration when no release variables are set`() {
        assertNull(ReleaseSigningConfig.resolve(emptyMap()))
    }

    @Test
    fun `rejects partial release signing configuration`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            ReleaseSigningConfig.resolve(
                environment(
                    ReleaseSigningConfig.KEYSTORE_PATH to "/tmp/release.jks",
                ),
            )
        }

        assertTrue(exception.message.orEmpty().contains(ReleaseSigningConfig.STORE_PASSWORD))
        assertFalse(exception.message.orEmpty().contains("store-password"))
    }

    @Test
    fun `rejects blank values as missing configuration`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            ReleaseSigningConfig.resolve(
                environment(
                    ReleaseSigningConfig.KEYSTORE_PATH to "/tmp/release.jks",
                    ReleaseSigningConfig.STORE_PASSWORD to " ",
                    ReleaseSigningConfig.KEY_ALIAS to "release-key",
                    ReleaseSigningConfig.KEY_PASSWORD to "key-password",
                ),
            )
        }

        assertTrue(exception.message.orEmpty().contains(ReleaseSigningConfig.STORE_PASSWORD))
    }

    @Test
    fun `requires an existing keystore for verification`() {
        val exception = assertThrows(IllegalStateException::class.java) {
            ReleaseSigningConfig.requireValid(completeEnvironment(), keystoreExists = { false })
        }

        assertTrue(exception.message.orEmpty().contains(ReleaseSigningConfig.KEYSTORE_PATH))
        assertFalse(exception.message.orEmpty().contains("/tmp/release.jks"))
    }

    @Test
    fun `verifies a complete configuration with an existing keystore`() {
        val credentials = ReleaseSigningConfig.requireValid(completeEnvironment(), keystoreExists = { true })

        assertEquals("release-key", credentials.keyAlias)
    }

    private fun completeEnvironment(): Map<String, String> =
        environment(
            ReleaseSigningConfig.KEYSTORE_PATH to "/tmp/release.jks",
            ReleaseSigningConfig.STORE_PASSWORD to "store-password",
            ReleaseSigningConfig.KEY_ALIAS to "release-key",
            ReleaseSigningConfig.KEY_PASSWORD to "key-password",
        )

    private fun environment(vararg entries: Pair<String, String>): Map<String, String> = entries.toMap()
}
