package me.minhael.design.data

class KeyChain(
    private val chain: MutableMap<String, Data.Element<out Any>> = hashMapOf()
): Map<String, Data.Element<out Any>> by chain {

    fun include(keyChain: KeyChain) = include(*keyChain.chain.values.toTypedArray())

    fun include(vararg elements: Data.Element<out Any>) {
        elements.forEach {
            val old = chain[it.name]
            if (old != null && old != it)
                throw IllegalArgumentException(String.format("Duplicated name: %s in %s and %s", it.name, old, it))
            chain[it.name] = it
        }
    }

    fun exclude(vararg names: String) {
        for (name in names) {
            chain.remove(name)
        }
    }

    companion object {
        @JvmField val DEFAULT = KeyChain()
    }
}
