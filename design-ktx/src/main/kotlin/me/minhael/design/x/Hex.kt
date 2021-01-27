package me.minhael.design.x

import me.minhael.design.misc.Hex

fun ByteArray.readBcd(offset: Int = 0, length: Int = size - offset) = Hex.readBcd(this, offset, length)
fun Long.writeBcd(buffer: ByteArray, offset: Int = 0, length: Int = buffer.size - offset) = Hex.writeBcd(this, buffer, offset, length)

fun Byte.asBcd() = Hex.fromBcd(this)
fun Int.asBcd() = Hex.toBcd(this)

fun ByteArray.asHex(offset: Int = 0, length: Int = size - offset) = Hex.readChars(this, offset, length)
fun CharArray.asHex(pad: Char = '0') = Hex.writeChars(this, pad)
fun String.asHex(pad: Char = '0') = Hex.writeStr(this, pad)

fun CharArray.writeHex(buffer: ByteArray, offset: Int = 0, length: Int = buffer.size - offset, pad: Char = '0') = Hex.writeChars(this, buffer, offset, length, pad)
fun String.writeHex(buffer: ByteArray, offset: Int = 0, length: Int = buffer.size - offset, pad: Char = '0') = Hex.writeStr(this, buffer, offset, length, pad)