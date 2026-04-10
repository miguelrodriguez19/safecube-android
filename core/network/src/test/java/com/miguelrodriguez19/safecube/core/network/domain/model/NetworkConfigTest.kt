package com.miguelrodriguez19.safecube.core.network.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class NetworkConfigTest {
    @Test
    fun `network config when base url ends with slash then creates config`() {
        val target = NetworkConfig(
            baseUrl = "https://api.example.com/",
            isDebug = true,
            connectTimeoutSeconds = 10,
            readTimeoutSeconds = 20,
            writeTimeoutSeconds = 30,
        )

        assertEquals("https://api.example.com/", target.baseUrl)
        assertEquals(true, target.isDebug)
        assertEquals(10L, target.connectTimeoutSeconds)
        assertEquals(20L, target.readTimeoutSeconds)
        assertEquals(30L, target.writeTimeoutSeconds)
    }

    @Test
    fun `network config when base url does not end with slash then throws illegal argument exception`() {
        assertThrows(IllegalArgumentException::class.java) {
            NetworkConfig(baseUrl = "https://api.example.com")
        }
    }
}
