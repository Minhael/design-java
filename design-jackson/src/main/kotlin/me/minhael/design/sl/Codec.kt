package me.minhael.design.sl

import com.fasterxml.jackson.core.TreeCodec
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import me.minhael.design.data.Data
import me.minhael.design.data.KeyChain
import me.minhael.design.data.MapData
import me.minhael.design.misc.Bytes
import me.minhael.design.misc.Chars
import java.io.IOException
import java.util.*

/**
 * Codec interface for converting data types <> json type
 */
interface Codec<T: Any> {

    fun encode(codec: TreeCodec, element: Data.Element<T>, obj: T, node: TreeNode)
    fun decode(element: Data.Element<T>, node: JsonNode): T

    class BooleanCodec : Codec<Boolean> {

        override fun encode(codec: TreeCodec, element: Data.Element<Boolean>, obj: Boolean, node: TreeNode) {
            if (node.isObject)
                (node as ObjectNode).put(element.name, obj)
            else if (node.isArray)
                (node as ArrayNode).add(obj)
        }

        override fun decode(element: Data.Element<Boolean>, node: JsonNode): Boolean {
            return node.isBoolean && node.asBoolean()
        }
    }

    class ByteCodec : Codec<Byte> {

        override fun encode(codec: TreeCodec, element: Data.Element<Byte>, obj: Byte, node: TreeNode) {
            if (node.isObject)
                (node as ObjectNode).put(element.name, obj.toShort())
            else if (node.isArray)
                (node as ArrayNode).add(obj.toInt())
        }

        override fun decode(element: Data.Element<Byte>, node: JsonNode): Byte {
            return if (node.canConvertToInt())
                (node.asInt() and 0xff).toByte()
            else
                0.toByte()
        }
    }

    class IntCodec : Codec<Int> {

        override fun encode(codec: TreeCodec, element: Data.Element<Int>, obj: Int, node: TreeNode) {
            if (node.isObject)
                (node as ObjectNode).put(element.name, obj)
            else if (node.isArray)
                (node as ArrayNode).add(obj)
        }

        override fun decode(element: Data.Element<Int>, node: JsonNode): Int {
            return if (node.canConvertToInt())
                node.asInt()
            else
                0
        }
    }

    class LongCodec : Codec<Long> {

        override fun encode(codec: TreeCodec, element: Data.Element<Long>, obj: Long, node: TreeNode) {
            if (node.isObject)
                (node as ObjectNode).put(element.name, obj)
            else if (node.isArray)
                (node as ArrayNode).add(obj)
        }

        override fun decode(element: Data.Element<Long>, node: JsonNode): Long {
            return if (node.canConvertToLong())
                node.asLong()
            else
                0L
        }
    }

    class CharacterCodec : Codec<Char> {

        override fun encode(codec: TreeCodec, element: Data.Element<Char>, obj: Char, node: TreeNode) {
            if (node.isObject)
                (node as ObjectNode).put(element.name, obj.toInt())
            else if (node.isArray)
                (node as ArrayNode).add(obj.toInt())
        }

        override fun decode(element: Data.Element<Char>, node: JsonNode): Char {
            return when {
                node.isInt -> node.asInt().toChar()
                else -> Character.MIN_CODE_POINT.toChar()
            }
        }
    }

    class BytesCodec : Codec<Bytes> {

        override fun encode(codec: TreeCodec, element: Data.Element<Bytes>, obj: Bytes, node: TreeNode) {
            if (node.isObject)
                (node as ObjectNode).put(element.name, obj.toArray())
            else if (node.isArray)
                (node as ArrayNode).add(obj.toArray())
        }

        override fun decode(element: Data.Element<Bytes>, node: JsonNode): Bytes {
            if (node.isBinary || node.isTextual) {
                try {
                    return Bytes.fromArray(node.binaryValue())
                } catch (e: IOException) {
                    //  Ignore
                }
            }
            return Bytes()
        }
    }

    class StringCodec : Codec<String> {

        override fun encode(codec: TreeCodec, element: Data.Element<String>, obj: String, node: TreeNode) {
            if (node.isObject)
                (node as ObjectNode).put(element.name, obj)
            else if (node.isArray)
                (node as ArrayNode).add(obj)
        }

        override fun decode(element: Data.Element<String>, node: JsonNode): String {
            return if (node.isTextual)
                node.asText()
            else
                ""
        }
    }

    class CharsCodec : Codec<Chars> {

        override fun encode(codec: TreeCodec, element: Data.Element<Chars>, obj: Chars, node: TreeNode) {
            if (node.isObject)
                (node as ObjectNode).put(element.name, obj.toString())
            else if (node.isArray)
                (node as ArrayNode).add(obj.toString())
        }

        override fun decode(element: Data.Element<Chars>, node: JsonNode): Chars {
            return if (node.isTextual)
                Chars.fromArray(node.asText().toCharArray())
            else
                Chars()
        }
    }

    class ListCodec<T: Any>(
        private val childElement: Data.Element<T>,
        private val jc: JsonCodec = JsonCodec.DEFAULT
    ) : Codec<MutableList<T>> {

        override fun encode(
            codec: TreeCodec,
            element: Data.Element<MutableList<T>>,
            obj: MutableList<T>,
            node: TreeNode
        ) {
            jc.findCodec(childElement)?.apply {
                val arrayNode = codec.createArrayNode() as JsonNode
                for (child in obj)
                    encode(codec, childElement, child, arrayNode)
                if (node.isObject)
                    (node as ObjectNode).set(element.name, arrayNode)
                else if (node.isArray)
                    (node as ArrayNode).add(arrayNode)
            }
        }

        override fun decode(element: Data.Element<MutableList<T>>, node: JsonNode): MutableList<T> {
            val codec = jc.findCodec(childElement)
            return if (codec != null && node.isArray) {
                val list = ArrayList<T>()
                val iterator = node.elements()
                while (iterator.hasNext())
                    list.add(codec.decode(childElement, iterator.next()))
                list
            } else
                arrayListOf()
        }
    }

    class DataCodec(
        private val chain: KeyChain = KeyChain.DEFAULT,
        private val jc: JsonCodec = JsonCodec.DEFAULT
    ) : Codec<Data> {

        override fun encode(codec: TreeCodec, element: Data.Element<Data>, obj: Data, node: TreeNode) {
            val child = JsonData.toJson(obj, codec, jc)
            if (node.isObject && child is JsonNode) {
                val parent = node as ObjectNode
                parent.set(element.name, child)
            } else if (node.isArray && child is JsonNode)
                (node as ArrayNode).add(child)
        }

        override fun decode(element: Data.Element<Data>, node: JsonNode): Data {
            return if (node.isObject) {
                MapData().also { JsonData.read(node, it, chain, jc) }
            } else
                MapData()
        }
    }
}
