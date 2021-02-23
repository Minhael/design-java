package me.minhael.design.test

import me.minhael.design.Charsets
import me.minhael.design.misc.Hex
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

interface CharsetsTest {

    val subject: Charsets

    @Test
    fun testEncode() {
        assertArrayEquals(bytes, subject.encode(str))
    }

    @Test
    fun testDecode() {
        assertEquals(str, subject.decode(bytes))
    }

    companion object {
        private const val str = "Денят \"Д\" за Диего Коста - Футбол свят - Испания"
        private val bytes = Hex.writeStr("B4D5DDEFE22022B42220D7D020B4D8D5D3DE20BADEE1E2D0202D20C4E3E2D1DEDB20E1D2EFE2202D20B8E1DFD0DDD8EF")
    }
}