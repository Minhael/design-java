package me.minhael.design.test

import me.minhael.design.props.Props
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.lang.RuntimeException

interface PropsTest {

    val subject: Props

    @BeforeEach
    fun setup() {
        subject.clear()
    }

    @AfterEach
    fun tearDown() {
        subject.clear()
    }

    @Test
    fun testProps() {
        subject.apply {
            put(pInt, vInt)
            put(pLong, vLong)
            put(pString, vStr)
            put(pBytes, vBytes)
            put(pByte, vByte)
            put(pChar, vChar)
            put(pChars, vChars)
            put(pBool, vBool)
        }

        subject.apply {
            assertEquals(vInt, get(pInt, 0))
            assertEquals(vLong, get(pLong, 0L))
            assertEquals(vStr, get(pString, ""))
            assertArrayEquals(vBytes, get(pBytes, byteArrayOf()))
            assertEquals(vByte, get(pByte, 0.toByte()))
            assertEquals(vChar, get(pChar, '\u0000'))
            assertArrayEquals(vChars, get(pChars, charArrayOf()))
            assertEquals(vBool, get(pBool, false))
        }

        subject.apply {
            assertTrue(has(pInt))
            assertTrue(has(pLong))
            assertTrue(has(pString))
            assertTrue(has(pBytes))
            assertTrue(has(pByte))
            assertTrue(has(pChar))
            assertTrue(has(pChars))
            assertTrue(has(pBool))
        }
    }

    @Test
    fun testDefaultValues() {
        subject.apply {
            assertFalse(has(pInt))
            assertFalse(has(pLong))
            assertFalse(has(pString))
            assertFalse(has(pBytes))
            assertFalse(has(pByte))
            assertFalse(has(pChar))
            assertFalse(has(pChars))
            assertFalse(has(pBool))
        }

        subject.apply {
            assertEquals(0, get(pInt, 0))
            assertEquals(0L, get(pLong, 0L))
            assertEquals("", get(pString, ""))
            assertArrayEquals(byteArrayOf(), get(pBytes, byteArrayOf()))
            assertEquals(0.toByte(), get(pByte, 0.toByte()))
            assertEquals('\u0000', get(pChar, '\u0000'))
            assertArrayEquals(charArrayOf(), get(pChars, charArrayOf()))
            assertEquals(false, get(pBool, false))
        }
    }

    @Test
    fun testCommit() {
        try {
            subject.commit {
                it.apply {
                    put(pInt, vInt)
                    put(pLong, vLong)
                    put(pString, vStr)
                    put(pBytes, vBytes)
                    put(pByte, vByte)
                    put(pChar, vChar)
                    put(pChars, vChars)
                    put(pBool, vBool)
                }
                throw RuntimeException()
            }
        } catch (e: RuntimeException) {
            //  Success
        }

        subject.apply {
            assertFalse(has(pInt))
            assertFalse(has(pLong))
            assertFalse(has(pString))
            assertFalse(has(pBytes))
            assertFalse(has(pByte))
            assertFalse(has(pChar))
            assertFalse(has(pChars))
            assertFalse(has(pBool))
        }

        subject.commit {
            it.apply {
                put(pInt, vInt)
                put(pLong, vLong)
                put(pString, vStr)
                put(pBytes, vBytes)
                put(pByte, vByte)
                put(pChar, vChar)
                put(pChars, vChars)
                put(pBool, vBool)
            }
        }

        subject.apply {
            assertEquals(vInt, get(pInt, 0))
            assertEquals(vLong, get(pLong, 0L))
            assertEquals(vStr, get(pString, ""))
            assertArrayEquals(vBytes, get(pBytes, byteArrayOf()))
            assertEquals(vByte, get(pByte, 0.toByte()))
            assertEquals(vChar, get(pChar, '\u0000'))
            assertArrayEquals(vChars, get(pChars, charArrayOf()))
            assertEquals(vBool, get(pBool, false))
        }
    }

    companion object {
        private const val pInt = "int"
        private const val pLong = "long"
        private const val pString = "string"
        private const val pBytes = "bytes"
        private const val pByte = "byte"
        private const val pChar = "char"
        private const val pChars = "chars"
        private const val pBool = "bool"

        private const val vInt = 1
        private const val vLong = 2L
        private const val vStr = "3"
        private val vBytes = byteArrayOf(0x4, 0x5)
        private const val vByte = 0x6.toByte()
        private const val vChar = 'g'
        private val vChars = "89".toCharArray()
        private const val vBool = true
    }
}