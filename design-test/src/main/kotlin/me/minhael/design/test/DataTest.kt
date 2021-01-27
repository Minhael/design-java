package me.minhael.design.test

import me.minhael.design.data.Data
import me.minhael.design.data.KeyChain
import me.minhael.design.data.MapData
import me.minhael.design.data.MutableData
import me.minhael.design.misc.Bytes
import me.minhael.design.misc.Chars
import org.junit.jupiter.api.*

abstract class DataTest {

    abstract fun new(): MutableData

    lateinit var data: MutableData

    @BeforeEach
    fun setup() {
        data = new()
    }

    @AfterEach
    fun tearDown() {
        data.clear()
    }

    @Test
    fun testData() {
        //  Prepare data
        insert(data)
        data[TVals.KEY_DATA] = new().apply { insert(this) }
        data[TVals.KEY_LIST] = ArrayList()
        for (i in 0..3) {
            data[TVals.KEY_LIST]?.add(new().apply { insert(this) })
        }

        //  Test size
        Assertions.assertEquals(10, data.size())

        //  Test keys, has, get
        assert(data)
        Assertions.assertTrue(data.has(TVals.KEY_DATA))
        Assertions.assertNotNull(data[TVals.KEY_DATA])
        data[TVals.KEY_DATA]?.apply { assert(this) }
        Assertions.assertTrue(data.has(TVals.KEY_LIST))
        Assertions.assertNotNull(data[TVals.KEY_LIST])
        data[TVals.KEY_LIST]?.apply {
            Assertions.assertEquals(4, size)
            forEach { assert(it) }
        }

        //  Remove
        data.apply {
            remove(TVals.KEY_BOOL)
            remove(TVals.KEY_INT)
            remove(TVals.KEY_LONG)
            remove(TVals.KEY_BYTE)
            remove(TVals.KEY_CHAR)
            remove(TVals.KEY_BYTES)
            remove(TVals.KEY_STR)
            remove(TVals.KEY_CHARS)
            remove(TVals.KEY_DATA)
            remove(TVals.KEY_LIST)
        }

        Assertions.assertEquals(0, data.size())
    }

    @Test
    fun testDefault() {
        data.apply {
            val keys = keys()
            Assertions.assertFalse(keys.contains(TVals.KEY_BOOL))
            Assertions.assertFalse(keys.contains(TVals.KEY_INT))
            Assertions.assertFalse(keys.contains(TVals.KEY_LONG))
            Assertions.assertFalse(keys.contains(TVals.KEY_BYTE))
            Assertions.assertFalse(keys.contains(TVals.KEY_CHAR))
            Assertions.assertFalse(keys.contains(TVals.KEY_BYTES))
            Assertions.assertFalse(keys.contains(TVals.KEY_STR))
            Assertions.assertFalse(keys.contains(TVals.KEY_CHARS))

            Assertions.assertFalse(has(TVals.KEY_BOOL))
            Assertions.assertFalse(has(TVals.KEY_INT))
            Assertions.assertFalse(has(TVals.KEY_LONG))
            Assertions.assertFalse(has(TVals.KEY_BYTE))
            Assertions.assertFalse(has(TVals.KEY_CHAR))
            Assertions.assertFalse(has(TVals.KEY_BYTES))
            Assertions.assertFalse(has(TVals.KEY_STR))
            Assertions.assertFalse(has(TVals.KEY_CHARS))

            Assertions.assertEquals(false, data[TVals.KEY_BOOL, { false }])
            Assertions.assertEquals(0, data[TVals.KEY_INT, { 0 }])
            Assertions.assertEquals(0L, data[TVals.KEY_LONG, { 0L }])
            Assertions.assertEquals(0.toByte(), data[TVals.KEY_BYTE, { 0.toByte() }])
            Assertions.assertEquals('\u0000', data[TVals.KEY_CHAR, { '\u0000' }])
            Assertions.assertEquals(Bytes(0x0), data[TVals.KEY_BYTES, { Bytes(0x0) }])
            Assertions.assertEquals("a", data[TVals.KEY_STR, { "a" }])
            Assertions.assertEquals(Chars('a'), data[TVals.KEY_CHARS, { Chars('a') }])
        }
    }

    @Test
    fun testSame() {
        insert(data)
        data[TVals.KEY_DATA] = new().apply { insert(this) }
        data[TVals.KEY_LIST] = ArrayList()
        for (i in 0..3) {
            data[TVals.KEY_LIST]?.add(new().apply { insert(this) })
        }

        val other = MapData().apply { insert(this) }
        other[TVals.KEY_DATA] = new().apply { insert(this) }
        other[TVals.KEY_LIST] = ArrayList()
        for (i in 0..3) {
            other[TVals.KEY_LIST]?.add(new().apply { insert(this) })
        }
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_LIST]?.get(1)?.set(TVals.KEY_BOOL, false)
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_LIST]?.get(1)?.set(TVals.KEY_BOOL, true)
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_LIST]?.removeAt(0)
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_LIST] = ArrayList()
        for (i in 0..3) {
            other[TVals.KEY_LIST]?.add(new().apply { insert(this) })
        }
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_DATA]?.set(TVals.KEY_BOOL, false)
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_DATA]?.set(TVals.KEY_BOOL, true)
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_BOOL] = false
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_BOOL] = true
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_INT] = Int.MAX_VALUE
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_INT] = 1
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_LONG] = Long.MAX_VALUE
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_LONG] = 2L
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_BYTE] = Byte.MAX_VALUE
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_BYTE] = 3.toByte()
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_CHAR] = Char.MAX_VALUE
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_CHAR] = 'd'
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_STR] = "test"
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_STR] = "Quick Brown Fox Jumps Over Lazy Dog"
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_BYTES] = Bytes(0x1, 0x2)
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_BYTES] = Bytes(0x9a.toByte(), 0xbc.toByte())
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())

        other[TVals.KEY_CHARS] = Chars('8', 'b')
        Assertions.assertNotEquals(data, other)
        other[TVals.KEY_CHARS] = Chars('7', '8')
        Assertions.assertEquals(data, other)
        Assertions.assertEquals(data.hashCode(), other.hashCode())
    }

    private fun insert(data: MutableData) {
        data.apply {
            set(TVals.KEY_BOOL, TVals.vBool)
            set(TVals.KEY_INT, TVals.vInt)
            set(TVals.KEY_LONG, TVals.vLong)
            set(TVals.KEY_BYTE, TVals.vByte)
            set(TVals.KEY_CHAR, TVals.vChar)
            set(TVals.KEY_BYTES, TVals.vBytes)
            set(TVals.KEY_STR, TVals.vStr)
            set(TVals.KEY_CHARS, TVals.vChars)
        }
    }

    private fun assert(data: Data) {
        data.apply {
            val keys = keys()
            Assertions.assertTrue(keys.contains(TVals.KEY_BOOL))
            Assertions.assertTrue(keys.contains(TVals.KEY_INT))
            Assertions.assertTrue(keys.contains(TVals.KEY_LONG))
            Assertions.assertTrue(keys.contains(TVals.KEY_BYTE))
            Assertions.assertTrue(keys.contains(TVals.KEY_CHAR))
            Assertions.assertTrue(keys.contains(TVals.KEY_STR))
            Assertions.assertTrue(keys.contains(TVals.KEY_BYTES))
            Assertions.assertTrue(keys.contains(TVals.KEY_CHARS))

            Assertions.assertTrue(has(TVals.KEY_BOOL))
            Assertions.assertTrue(has(TVals.KEY_INT))
            Assertions.assertTrue(has(TVals.KEY_LONG))
            Assertions.assertTrue(has(TVals.KEY_BYTE))
            Assertions.assertTrue(has(TVals.KEY_CHAR))
            Assertions.assertTrue(has(TVals.KEY_STR))
            Assertions.assertTrue(has(TVals.KEY_BYTES))
            Assertions.assertTrue(has(TVals.KEY_CHARS))

            Assertions.assertEquals(TVals.vBool, data[TVals.KEY_BOOL])
            Assertions.assertEquals(TVals.vInt, data[TVals.KEY_INT])
            Assertions.assertEquals(TVals.vLong, data[TVals.KEY_LONG])
            Assertions.assertEquals(TVals.vByte, data[TVals.KEY_BYTE])
            Assertions.assertEquals(TVals.vChar, data[TVals.KEY_CHAR])
            Assertions.assertEquals(TVals.vStr, data[TVals.KEY_STR])
            Assertions.assertEquals(TVals.vBytes, data[TVals.KEY_BYTES])
            Assertions.assertEquals(TVals.vChars, data[TVals.KEY_CHARS])
        }
    }

    companion object {

        @BeforeAll
        @JvmStatic
        fun init() {
            KeyChain.DEFAULT.include(
                TVals.KEY_BOOL,
                TVals.KEY_INT,
                TVals.KEY_LONG,
                TVals.KEY_BYTE,
                TVals.KEY_CHAR,
                TVals.KEY_BYTES,
                TVals.KEY_STR,
                TVals.KEY_CHARS,
                TVals.KEY_DATA,
                TVals.KEY_LIST
            )
        }
    }
}