package me.minhael.design.props

import me.minhael.design.sl.Serializer
import me.minhael.design.sl.TypeReference
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class MapProps(
        private val map: MutableMap<String, Any> = hashMapOf()
) : Props {

    override fun <T> commit(callable: (Store) -> T): T {
        val cache = MapProps()
        val rt = callable(cache)
        map.putAll(cache.map)
        return rt
    }

    override fun <T : Any> put(key: String, value: T) {
        map[key] = value
    }

    override fun <T : Any> get(key: String, defValue: T): T {
        return map[key]?.let { defValue::class.java.cast(it) } ?: defValue
    }

    override fun has(key: String): Boolean {
        return map.containsKey(key)
    }

    override fun clear() {
        map.clear()
    }

    companion object {

        private val typeClass = object : TypeReference<HashMap<String, Any>>() {}

        @JvmStatic fun serialize(serializer: Serializer, props: MapProps): ByteArray {
            return props.map.let { value ->
                ByteArrayOutputStream().use {
                    serializer.serialize(HashMap(value), it)
                    it.toByteArray()
                }
            }
        }

        @JvmStatic fun deserialize(serializer: Serializer, bytes: ByteArray): MapProps {
            return ByteArrayInputStream(bytes).use {
                MapProps(serializer.deserialize(it, typeClass))
            }
        }
    }
}