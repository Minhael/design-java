package me.minhael.design.misc

import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class HexTest {

    @Test
    fun testReadBcdBytes() {
        val input = byteArrayOf(0x00, 0x01, 0x23, 0x45, 0x67, 0x89.toByte(), 0x00)

        val e1 = 123456789L
        assertEquals(e1, Hex.readBcd(input, 1, 5))

        val e2 = 123L
        assertEquals(e2, Hex.readBcd(input, 1, 2))

        val e3 = 123L
        assertEquals(e3, Hex.readBcd(input, 0, 3))

        val e4 = 8900L
        assertEquals(e4, Hex.readBcd(input, 5, 2))
    }

    @Test
    fun testWriteBcdBytes() {
        val input = 123456789L
        val result = ByteArray(7)

        val e1 = byteArrayOf(0x00, 0x01, 0x23, 0x45, 0x67, 0x89.toByte(), 0x00)
        assertEquals(5, Hex.writeBcd(input, result, 1, 5))
        assertArrayEquals(e1, result)

        result.fill(0)
        val e2 = byteArrayOf(0x00, 0x00, 0x01, 0x23, 0x45, 0x67, 0x89.toByte())
        assertEquals(5, Hex.writeBcd(input, result))
        assertArrayEquals(e2, result)

        result.fill(0)
        val e3 = byteArrayOf(0x45, 0x67, 0x89.toByte(), 0x00, 0x00, 0x00, 0x00)
        assertEquals(3, Hex.writeBcd(input, result, 0, 3))
        assertArrayEquals(e3, result)
    }

    @Test
    fun testfromBcd() {
        val input = 0x89.toByte()
        val base10 = 89
        assertEquals(base10, Hex.fromBcd(input))
    }

    @Test
    fun testToBcd() {
        val input = 89
        val expected = 0x89.toByte()
        assertEquals(expected, Hex.toBcd(input))
    }

    @Test
    fun testReadChars() {
        val input = byteArrayOf(0x25, 0xab.toByte(), 0xe0.toByte(), 0xf1.toByte())
        val e1 = "25abe0f1".toCharArray()
        assertArrayEquals(e1, Hex.readChars(input))
    }

    @Test
    fun testWriteChars() {
        val input = "25abe0f".toCharArray()
        val result = ByteArray(7)
        val expected = byteArrayOf(0x0, 0x25, 0xab.toByte(), 0xe0.toByte(), 0xff.toByte(), 0, 0)
        assertEquals(4, Hex.writeChars(input, result, 1, 6, 'f'))
        assertArrayEquals(expected, result)
    }
}