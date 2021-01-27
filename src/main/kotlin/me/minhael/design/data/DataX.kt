package me.minhael.design.data

import me.minhael.design.misc.Hex

object DataX {

    @JvmStatic fun merge(self: MutableData, other: Data, policy: Policy = Policy.FILL, keyChain: KeyChain = KeyChain.DEFAULT) {
        other.keys(keyChain).forEach {
            policy.transfer(it, other, self)
        }
    }

    @JvmStatic fun slice(data: Data, into: MutableData, vararg keys: Data.Element<*>) {
        merge(into, data, Policy.FETCH, KeyChain().apply { include(*keys) })
    }

    @JvmStatic fun dump(data: Data, builder: StringBuilder = StringBuilder(), chain: KeyChain = KeyChain.DEFAULT): StringBuilder {
        builder.appendLine("--------------------------------------------------------------------------------")
        dumpData(builder, chain, data)
        builder.appendLine("--------------------------------------------------------------------------------")
        return builder
    }

    private fun dumpData(builder: StringBuilder, chain: KeyChain, data: Data) {
        data.keys(chain).forEach { key -> dumpValue(builder, chain, key.name, data[key]) }
    }

    private fun <T> dumpValue(builder: StringBuilder, chain: KeyChain, name: String, value: T) {
        when (value) {
            is ByteArray -> {
                builder.append("%-40s".format(name))
                builder.append(Hex.readChars(value))
                builder.appendLine()
            }
            is CharArray -> {
                builder.append("%-40s".format(name))
                builder.append(value.joinToString(","))
                builder.appendLine()
            }
            is Data -> {
                builder.appendLine(name)
                builder.append(
                    StringBuilder()
                        .apply { dumpData(this, chain, value) }
                        .replace(Regex("(?m)^"), "\t")
                )
            }
            is List<*> -> {
                value.forEachIndexed { index, child ->
                    dumpValue(builder, chain, "$name[$index]", child)
                }
            }
            else -> {
                builder.append("%-40s".format(name))
                builder.append(value.toString())
                builder.appendLine()
            }
        }
    }
}