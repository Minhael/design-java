package me.minhael.design.x

import me.minhael.design.data.*
import me.minhael.design.db.Contracts
import me.minhael.design.sl.Serializer
import java.io.InputStream

fun MutableData.fill(other: Data, keyChain: KeyChain = KeyChain.DEFAULT) = DataX.merge(this, other, Policy.FILL, keyChain)
fun MutableData.fetch(other: Data, keyChain: KeyChain = KeyChain.DEFAULT) = DataX.merge(this, other, Policy.FETCH, keyChain)
fun Data.slice(other: MutableData, vararg keys: Data.Element<*>) = DataX.slice(this, other, *keys)
fun Data.dump(builder: StringBuilder = StringBuilder(), chain: KeyChain = KeyChain.DEFAULT) = DataX.dump(this, builder, chain)

fun MutableMap<String, Any>.pack() = MapData(this)

inline fun <reified T : Any> Contracts.of(): T? = this.of(T::class.java)

inline fun <reified T> Serializer.deserialize(inputStream: InputStream) = deserialize(inputStream, T::class.java)