package me.minhael.design.misc

import java.io.Serializable

class Bytes(
    private vararg val array: Byte
): Serializable {

    fun toArray() = ByteArray(array.size) { array[it] }

    override fun equals(other: Any?): Boolean {
        return other is Bytes && array.contentEquals(other.array)
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }

    override fun toString(): String {
        return Hex.readChars(array).concatToString()
    }

    companion object {
        fun fromArray(array: ByteArray) = Bytes(*array)
    }
}