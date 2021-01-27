package me.minhael.design.data

/**
 * Implement [Data] using heterogeneous [Map] with [Long] key
 */
class MapData(
    private val map: MutableMap<String, Any> = hashMapOf()
) : MutableData {

    override fun keys(keySet: Map<String, Data.Element<out Any>>) = map.keys.mapNotNull { keySet[it] }

    override fun has(element: Data.Element<out Any>) = map.containsKey(element.resolve().name)

    override fun size() = map.size

    override fun <T: Any> get(key: Data.Element<T>): T? {
        return key.resolve().run {
            map[name]?.let { key.typeRef.cast(it) }
        }
    }

    override fun <T: Any> set(key: Data.Element<T>, value: T): T? {
        return map.put(key.resolve().name, value)?.let { key.typeRef.cast(it) }
    }

    override fun <T: Any> remove(key: Data.Element<T>): T? {
        return map.remove(key.resolve().name)?.let { key.typeRef.cast(it) }
    }

    override fun clear() = map.clear()

    override fun hashCode(): Int {
        return map.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return other is Data && Data.contentsEquals(this, other)
    }

    override fun toString(): String {
        return StringBuilder()
            .appendLine(super.toString())
            .let { DataX.dump(this, it) }
            .toString()
    }
}