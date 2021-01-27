package me.minhael.design.x

import me.minhael.design.misc.Bytes
import me.minhael.design.misc.Chars

fun ByteArray.toBytes() = Bytes.fromArray(this)
fun CharArray.toChars() = Chars.fromArray(this)