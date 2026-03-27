package com.miguelrodriguez19.safecube.core.vault.data.crypto

import java.nio.charset.StandardCharsets
import java.util.UUID
import org.junit.Assert.assertArrayEquals
import org.junit.Test

class SecureItemPayloadAadFactoryTest {

    private val target = SecureItemPayloadAadFactory()

    @Test
    fun `create when identifiers are provided then returns canonical aad bytes`() {
        val accountId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa")
        val logicalItemId = UUID.fromString("11111111-1111-1111-1111-111111111111")

        val result = target.create(
            accountId = accountId,
            logicalItemId = logicalItemId,
            payloadVersion = 7,
        )

        assertArrayEquals(
            "accountId:$accountId|logicalItemId:$logicalItemId|payloadVersion:7"
                .toByteArray(StandardCharsets.UTF_8),
            result,
        )
    }
}
