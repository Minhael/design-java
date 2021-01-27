package me.minhael.design.misc

import java.io.Serializable

class Chars(
    private vararg val array: Char
): Serializable {

    fun toArray() = CharArray(array.size) { array[it] }

    override fun equals(other: Any?): Boolean {
        return other is Chars && array.contentEquals(other.array)
    }

    override fun hashCode(): Int {
        return array.contentHashCode()
    }

    override fun toString(): String {
        return array.concatToString()
    }

    companion object {
        fun fromArray(array: CharArray) = Chars(*array)
    }
}