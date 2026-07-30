package com.miguelrodriguez19.safecube.buildlogic

import java.util.Properties
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class AppVersionTest {
    @Test
    fun `accepts a stable version`() {
        val version = AppVersionParser.fromProperties(properties("0.1.0", "1"))

        assertEquals(AppVersion("0.1.0", 1), version)
    }

    @Test
    fun `accepts a prerelease version`() {
        val version = AppVersionParser.fromProperties(properties("1.0.0-rc.1", "12"))

        assertEquals(AppVersion("1.0.0-rc.1", 12), version)
    }

    @Test
    fun `accepts build metadata`() {
        val version = AppVersionParser.fromProperties(properties("1.0.0+android.1", "13"))

        assertEquals(AppVersion("1.0.0+android.1", 13), version)
    }

    @Test
    fun `rejects an invalid semver`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            AppVersionParser.fromProperties(properties("1.0", "1"))
        }

        assertTrue(exception.message.orEmpty().contains("VERSION_NAME"))
    }

    @Test
    fun `rejects an invalid version code`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            AppVersionParser.fromProperties(properties("0.1.0", "0"))
        }

        assertTrue(exception.message.orEmpty().contains("VERSION_CODE"))
    }

    @Test
    fun `rejects a missing property`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            AppVersionParser.fromProperties(Properties())
        }

        assertTrue(exception.message.orEmpty().contains("VERSION_NAME"))
    }

    @Test
    fun `rejects an empty property`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            AppVersionParser.fromProperties(properties(" ", "1"))
        }

        assertTrue(exception.message.orEmpty().contains("VERSION_NAME"))
    }

    private fun properties(versionName: String, versionCode: String): Properties =
        Properties().apply {
            setProperty(AppVersionParser.VERSION_NAME_KEY, versionName)
            setProperty(AppVersionParser.VERSION_CODE_KEY, versionCode)
        }
}
