package com.miguelrodriguez19.safecube.core.vault.data.session

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class VaultInMemoryKekStoreTest {

    private val target = VaultInMemoryKekStore()

    @Test
    fun `replace when kek is provided then stores independent snapshot`() {
        val source = byteArrayOf(1, 2, 3, 4)

        target.replace(source)
        source.fill(9)

        assertArrayEquals(byteArrayOf(1, 2, 3, 4), target.snapshot())
    }

    @Test
    fun `clear when kek exists then zeroizes current reference and removes snapshot`() {
        target.replace(byteArrayOf(5, 6, 7, 8))
        val leakedReference = requireNotNull(target.currentReference())

        target.clear()

        assertTrue(leakedReference.all { it == 0.toByte() })
        assertNull(target.currentReference())
        assertNull(target.snapshot())
    }

    @Test
    fun `clear when store is already empty then keeps store empty`() {
        target.clear()

        assertNull(target.currentReference())
        assertNull(target.snapshot())
    }
}
