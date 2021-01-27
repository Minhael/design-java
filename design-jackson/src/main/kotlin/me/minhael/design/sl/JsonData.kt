package me.minhael.design.sl

import com.fasterxml.jackson.core.TreeCodec
import com.fasterxml.jackson.core.TreeNode
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import me.minhael.design.data.Data
import me.minhael.design.data.KeyChain
import me.minhael.design.data.MutableData
import java.io.InputStream
import java.io.OutputStream

/**
 * Helpers to se/deserialize Data <> JSON
 */
object JsonData {

    @JvmStatic fun read(
        json: String,
        data: MutableData,
        mapper: ObjectMapper,
        chain: KeyChain = KeyChain.DEFAULT,
        jc: JsonCodec = JsonCodec.DEFAULT
    ) {
        read(mapper.readTree(json), data, chain, jc)
    }

    @JvmStatic fun read(
        input: InputStream,
        data: MutableData,
        mapper: ObjectMapper,
        chain: KeyChain = KeyChain.DEFAULT,
        jc: JsonCodec = JsonCodec.DEFAULT
    ) {
        read(mapper.readTree(input), data, chain, jc)
    }

    @JvmStatic fun read(
        node: JsonNode,
        data: MutableData,
        chain: KeyChain = KeyChain.DEFAULT,
        jc: JsonCodec = JsonCodec.DEFAULT
    ) {
        if (node.isArray)
            node.elements().forEach { read(it, data, chain, jc) }

        if (node.isObject)
            node.fields().forEach {
                chain[it.key]?.apply { decode(this, it.value, data, jc) }
            }
    }

    @JvmStatic fun toJson(
        data: Data,
        codec: TreeCodec,
        jc: JsonCodec = JsonCodec.DEFAULT
    ): TreeNode {
        val root = codec.createObjectNode()
        for (key in data.keys())
            encode(data, key, root, codec, jc)
        return root
    }

    @JvmStatic fun write(
        data: Data,
        output: OutputStream,
        mapper: ObjectMapper,
        jc: JsonCodec = JsonCodec.DEFAULT
    ) {
        mapper.writeValue(output, toJson(data, mapper, jc))
    }

    @JvmStatic fun toString(
        data: Data,
        mapper: ObjectMapper,
        jc: JsonCodec = JsonCodec.DEFAULT
    ): String {
        return mapper.writeValueAsString(toJson(data, mapper, jc))
    }

    private fun <T : Any> decode(
        key: Data.Element<T>,
        node: JsonNode,
        data: MutableData,
        jc: JsonCodec = JsonCodec.DEFAULT
    ) {
        jc.findCodec(key)?.decode(key, node)?.apply { data[key] = this }
    }

    private fun <T : Any> encode(
        data: Data,
        key: Data.Element<T>,
        root: TreeNode,
        codec: TreeCodec,
        jc: JsonCodec = JsonCodec.DEFAULT
    ) {
        data[key]?.also { jc.findCodec(key)?.encode(codec, key, it, root) }
    }
}
