package me.minhael.design.sl

import me.minhael.design.data.Data
import me.minhael.design.misc.Bytes
import me.minhael.design.misc.Chars
import me.minhael.design.misc.Klass
import java.util.*

/**
 * Codec table to find the suitable codec
 */
class JsonCodec {

    private val chain = HashMap<Data.Element<*>, Codec<*>>()

    inline fun <T: Any, reified S: Codec<T>> findCodec(element: Data.Element<T>): S? {
        return find(element)?.let { it as S }
    }

    //  Recursively find for ShadowElement
    fun find(element: Data.Element<*>): Codec<*>? {
        val real = element.resolve()
        return if (element != real)
            chain[element] ?: find(real)
        else
            chain[element] ?: getDefault(real)
    }

    fun <T: Any> register(element: Data.Element<T>, codec: Codec<T>) {
        chain[element] = codec
    }

    companion object {

        val DEFAULT = JsonCodec()

        private val codecBoolean: Codec<Boolean> = Codec.BooleanCodec()
        private val codecByte: Codec<Byte> = Codec.ByteCodec()
        private val codecInt: Codec<Int> = Codec.IntCodec()
        private val codecLong: Codec<Long> = Codec.LongCodec()
        private val codecChar: Codec<Char> = Codec.CharacterCodec()
        private val codecBytes: Codec<Bytes> = Codec.BytesCodec()
        private val codecString: Codec<String> = Codec.StringCodec()
        private val codecChars: Codec<Chars> = Codec.CharsCodec()
        private val codecData: Codec<Data> = Codec.DataCodec()

        private fun <T: Any> getDefault(element: Data.Element<T>): Codec<*>? {
            val objClass = element.typeRef
            return when {
                Klass.isSubClass(objClass, Byte::class.java) -> codecByte
                Klass.isSubClass(objClass, String::class.java) -> codecString
                Klass.isSubClass(objClass, Long::class.java) -> codecLong
                Klass.isSubClass(objClass, Int::class.java) -> codecInt
                Klass.isSubClass(objClass, Char::class.java) -> codecChar
                Klass.isSubClass(objClass, Boolean::class.java) -> codecBoolean
                Klass.isSubClass(objClass, Bytes::class.java) -> codecBytes
                Klass.isSubClass(objClass, Chars::class.java) -> codecChars
                Klass.isSubClass(objClass, Data::class.java) -> codecData
                else -> null
            }
        }
    }
}
