package me.minhael.design.sl

import com.fasterxml.jackson.databind.JsonNode
import me.minhael.design.data.Data
import me.minhael.design.data.KeyChain
import me.minhael.design.data.MapData
import me.minhael.design.data.MutableData
import me.minhael.design.misc.Bytes
import me.minhael.design.misc.Chars
import me.minhael.design.test.TVals
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.*

internal class JsonCodecTest {

    private val mapper = JacksonSerializer.base()

    lateinit var root: JsonNode

    @BeforeEach
    fun setup() {
        root = mapper.createObjectNode()
    }

    @Test
    fun testBoolCodec() {
        testFor(TVals.KEY_BOOL, TVals.vBool) { str, obj ->
            assertEquals("{\"${TVals.KEY_BOOL.name}\":${TVals.vBool}}", str)
            assertEquals(TVals.vBool, obj)
        }
    }

    @Test
    fun testIntCodec() {
        testFor(TVals.KEY_INT, TVals.vInt) { str, obj ->
            assertEquals("{\"${TVals.KEY_INT.name}\":${TVals.vInt}}", str)
            assertEquals(TVals.vInt, obj)
        }
    }

    @Test
    fun testLongCodec() {
        testFor(TVals.KEY_LONG, TVals.vLong) { str, obj ->
            assertEquals("{\"${TVals.KEY_LONG.name}\":${TVals.vLong}}", str)
            assertEquals(TVals.vLong, obj)
        }
    }

    @Test
    fun testByteCodec() {
        testFor(TVals.KEY_BYTE, TVals.vByte) { str, obj ->
            assertEquals("{\"${TVals.KEY_BYTE.name}\":${TVals.vByte}}", str)
            assertEquals(TVals.vByte, obj)
        }
    }

    @Test
    fun testCharCodec() {
        testFor(TVals.KEY_CHAR, TVals.vChar) { str, obj ->
            assertEquals("{\"${TVals.KEY_CHAR.name}\":${TVals.vChar.toInt()}}", str)
            assertEquals(TVals.vChar, obj)
        }
    }

    @Test
    fun testBytesCodec() {
        testFor(TVals.KEY_BYTES, TVals.vBytes) { str, obj ->
            assertEquals("{\"${TVals.KEY_BYTES.name}\":\"${Base64.getEncoder().encodeToString(TVals.vBytes.toArray())}\"}", str)
            assertEquals(TVals.vBytes, obj)
        }
    }

    @Test
    fun testStrCodec() {
        testFor(TVals.KEY_STR, TVals.vStr) { str, obj ->
            assertEquals("{\"${TVals.KEY_STR.name}\":\"${TVals.vStr}\"}", str)
            assertEquals(TVals.vStr, obj)
        }
    }

    @Test
    fun testCharsCodec() {
        testFor(TVals.KEY_CHARS, TVals.vChars) { str, obj ->
            assertEquals("{\"${TVals.KEY_CHARS.name}\":\"${TVals.vChars}\"}", str)
            assertEquals(TVals.vChars, obj)
        }
    }

    @Test
    fun testDataCodec() {
        val data = MapData().apply { insert(this) }
        data[TVals.KEY_DATA] = MapData().apply { insert(this) }
        data[TVals.KEY_LIST] = ArrayList()
        for (i in 0..3) {
            data[TVals.KEY_LIST]?.add(MapData().apply { insert(this) })
        }

        testFor(TVals.KEY_DATA, data) { _, obj -> assertEquals(data, obj) }
    }

    private fun <T: Any>testFor(
        key: Data.Element<T>,
        value: T,
        assertion: (String, T) -> Unit,
    ) {
        JsonCodec.DEFAULT.findCodec(key)
            ?.apply {
                encode(mapper, key, value, root)
                val json = mapper.writeValueAsString(root)
                val node = mapper.readTree(json)
                assertion(json, decode(key, node[key.name]))
            }
            ?: fail("Missing default codec for ${key.typeRef}")
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
            JsonCodec.DEFAULT.register(TVals.KEY_LIST, Codec.ListCodec(TVals.KEY_DATA))
        }
    }
}