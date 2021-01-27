package me.minhael.design.misc

object Hex {

    /**
     * 0x01 0x23 0x56 -> 12345
     */
    @JvmStatic fun readBcd(buffer: ByteArray, offset: Int = 0, length: Int = buffer.size - offset): Long {
        var value = 0L

        for (i in 0 until length) {
            value *= 100
            value += fromBcd(buffer[offset + i])
        }

        return value
    }

    /**
     * 12345 -> 0x01 0x23 0x45
     */
    @JvmStatic fun writeBcd(value: Long, buffer: ByteArray, offset: Int = 0, length: Int = buffer.size - offset): Int {
        var n = value
        for (i in length - 1 downTo 0) {
            buffer[offset + i] = toBcd(n.rem(100).toInt())
            n /= 100
            if (n == 0L)
                return length - i
        }
        return length
    }

    /**
     * 0x89 -> 89
     */
    @JvmStatic fun fromBcd(byte: Byte): Int {
        val n = byte.toInt()
        return (n.shr(4) and 0x0f) * 10 + (n and 0x0f)
    }

    /**
     * 89 -> 0x89
     */
    @JvmStatic fun toBcd(value: Int): Byte {
        return ((value / 10).rem(10).shl(4) or value.rem(10)).toByte()
    }

    /**
     * 0x01 0x23 0x45 -> "012345"
     */
    @JvmStatic fun readChars(buffer: ByteArray, offset: Int = 0, length: Int = buffer.size - offset): CharArray {
        val rt = CharArray(length * 2) { '\u0000' }

        for (i in 0 until length) {
            val b = buffer[offset + i].toInt() and 0xff
            rt[i * 2] = hexArray[b.shr(4).rem(16)]
            rt[i * 2 + 1] = hexArray[(b and 0x0f).rem(16)]
        }

        return rt
    }

    /**
     * "12345" -> 0x12 0x34 0x50
     */
    @JvmStatic fun writeChars(value: CharArray, buffer: ByteArray, offset: Int = 0, length: Int = buffer.size - offset, pad: Char = '0'): Int {
        for (i in 0 until length) {
            if (i * 2 < value.size)
                buffer[offset + i] = toHexByte(value[i * 2], value.getOrElse(i * 2 + 1) { pad })
            else
                return i
        }
        return length
    }

    private fun toHexByte(upper: Char, lower: Char): Byte {
        return ((hexMap[upper] ?: 0x0).shl(4) or (hexMap[lower] ?: 0x0)).toByte()
    }

    private val hexArray = charArrayOf('0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f')
    private val hexMap = mapOf(
        '0' to 0,
        '1' to 1,
        '2' to 2,
        '3' to 3,
        '4' to 4,
        '5' to 5,
        '6' to 6,
        '7' to 7,
        '8' to 8,
        '9' to 9,
        'a' to 0xa,
        'b' to 0xb,
        'c' to 0xc,
        'd' to 0xd,
        'e' to 0xe,
        'f' to 0xf,
        'A' to 0xa,
        'B' to 0xb,
        'C' to 0xc,
        'D' to 0xd,
        'E' to 0xe,
        'F' to 0xf
    )

    /*
        Helpers
     */
    @JvmStatic fun readStr(buffer: ByteArray, offset: Int = 0, length: Int = buffer.size - offset): String {
        return readChars(buffer, offset, length).concatToString()
    }

    @JvmStatic fun writeStr(value: String, buffer: ByteArray, offset: Int = 0, length: Int = buffer.size - offset, pad: Char = '0'): Int {
        return writeChars(value.toCharArray(), buffer, offset, length, pad)
    }

    @JvmStatic fun writeChars(value: CharArray, pad: Char = '0'): ByteArray {
        return ByteArray((value.size + 1) / 2).also { Hex.writeChars(value, it, pad = pad) }
    }

    @JvmStatic fun writeStr(value: String, pad: Char = '0'): ByteArray {
        return writeChars(value.toCharArray(), pad)
    }
}