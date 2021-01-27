package me.minhael.design.test

import me.minhael.design.data.Data
import me.minhael.design.data.MapData
import me.minhael.design.data.MutableData
import me.minhael.design.misc.Bytes
import me.minhael.design.misc.Chars
import me.minhael.design.misc.Hex
import java.io.Serializable

object TVals {
    val KEY_BOOL = Data.boolean("bool", "Bool")
    val KEY_INT = Data.int("int", "Int")
    val KEY_LONG = Data.long("long", "Long")
    val KEY_BYTE = Data.byte("byte", "Byte")
    val KEY_CHAR = Data.char("char", "Char")
    val KEY_STR = Data.string("str", "String")
    val KEY_CHARS = Data.chars("chars", "Chars")
    val KEY_BYTES = Data.bytes("bytes", "Bytes")
    val KEY_DATA = Data.data("data", "Data")
    val KEY_LIST = Data.list<MutableData>("list", "List")

    const val vBool = true
    const val vInt = 1
    const val vLong = 2L
    const val vByte = 0x3.toByte()
    const val vChar = 'd'
    const val vStr = "Quick Brown Fox Jumps Over Lazy Dog"
    val vChars = Chars(*"78".toCharArray())
    val vBytes = Bytes(*Hex.writeStr("9abc"))

    fun vList() = listOf(vBool, vInt, vLong, vByte, vChar, vStr, vChars, vBytes)
    fun vMap() = mapOf(
        KEY_BOOL.name to vBool,
        KEY_INT.name to vInt,
        KEY_LONG.name to vLong,
        KEY_BYTE.name to vByte,
        KEY_CHAR.name to vChar,
        KEY_STR.name to vStr,
        KEY_CHARS.name to vChars,
        KEY_BYTES.name to vBytes
    )
    fun vData() = MapData().apply {
        set(KEY_BOOL, vBool)
        set(KEY_INT, vInt)
        set(KEY_LONG, vLong)
        set(KEY_BYTE, vByte)
        set(KEY_CHAR, vChar)
        set(KEY_STR, vStr)
        set(KEY_CHARS, vChars)
        set(KEY_BYTES, vBytes)
    }
    fun vComplexList() = listOf(vData(), vMap(), vData())
    fun vComplexMap() = mapOf("d1" to vData(), "l1" to vList(), "d2" to vData())
    fun vComplexCollection() = listOf(
        mapOf(
            "c1" to listOf(vComplexList(), vComplexMap()),
            "c2" to listOf(vComplexList(), vComplexMap())
        ),
        mapOf(
            "c3" to listOf(vComplexList(), vComplexMap()),
            "c4" to listOf(vComplexList(), vComplexMap())
        )
    )

    data class A(
        val bool: Boolean = vBool,
        val int: Int = vInt,
        val long: Long = vLong,
        val byte: Byte = vByte,
        val char: Char = vChar,
        val str: String = vStr,
        val chars: CharArray = vChars.toArray(),
        val bytes: ByteArray = vBytes.toArray(),
        val data: Data = vData()
    ): Serializable {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as A

            if (bool != other.bool) return false
            if (int != other.int) return false
            if (long != other.long) return false
            if (byte != other.byte) return false
            if (char != other.char) return false
            if (str != other.str) return false
            if (!chars.contentEquals(other.chars)) return false
            if (!bytes.contentEquals(other.bytes)) return false
            if (data != other.data) return false

            return true
        }

        override fun hashCode(): Int {
            var result = bool.hashCode()
            result = 31 * result + int
            result = 31 * result + long.hashCode()
            result = 31 * result + byte
            result = 31 * result + char.hashCode()
            result = 31 * result + str.hashCode()
            result = 31 * result + chars.contentHashCode()
            result = 31 * result + bytes.contentHashCode()
            result = 31 * result + data.hashCode()
            return result
        }
    }

    open class B(
        val list: List<Any> = vList(),
        val map: Map<String, Any> = vMap(),
    ): Serializable {

        override fun hashCode(): Int {
            var result = list.hashCode()
            result = 31 * result + map.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as B

            if (list != other.list) return false
            if (map != other.map) return false

            return true
        }
    }

    class C(val a: A = A()): B(), Serializable {

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + a.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as C

            if (a != other.a) return false

            return true
        }
    }
}