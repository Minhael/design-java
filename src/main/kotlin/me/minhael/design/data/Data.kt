package me.minhael.design.data

import me.minhael.design.data.Data.Element
import me.minhael.design.misc.Bytes
import me.minhael.design.misc.Chars

/**
 * Centralize data container of generic types. Key of [Element] is required to
 */
interface Data {

    fun keys(keySet: Map<String, Element<out Any>> = KeyChain.DEFAULT): Collection<Element<out Any>>
    fun has(element: Element<out Any>): Boolean
    operator fun <T: Any> get(key: Element<T>): T?
    operator fun <T: Any> get(key: Element<T>, defValue: () -> T) = get(key) ?: defValue.invoke()
    fun size(): Int

    /**
     * Types of elements that allow to store inside [Data]
     */
    interface Element<T: Any> {
        val name: String
        val commonName: String
        val typeRef: Class<T>

        fun resolve(): Element<T> = this
    }

    private class RealElement<T: Any>(
        override val name: String,
        override val commonName: String,
        override val typeRef: Class<T>
    ) : Element<T> {
        override fun toString(): String {
            return "RealElement(name='$name', commonName='$commonName', typeRef=$typeRef)"
        }
    }

    private class ShadowElement<T: Any>(
        override val name: String,
        override val commonName: String,
        private val element: Element<T>
    ) : Element<T> by element {
        override fun resolve() = element
        override fun toString(): String {
            return "ShadowElement(name='$name', commonName='$commonName', element=$element)"
        }
    }

    companion object {

        @JvmStatic fun boolean(name: String, commonName: String) = create<Boolean>(name, commonName)
        @JvmStatic fun int(name: String, commonName: String) = create<Int>(name, commonName)
        @JvmStatic fun long(name: String, commonName: String) = create<Long>(name, commonName)
        @JvmStatic fun byte(name: String, commonName: String) = create<Byte>(name, commonName)
        @JvmStatic fun char(name: String, commonName: String) = create<Char>(name, commonName)
        @JvmStatic fun string(name: String, commonName: String) = create<String>(name, commonName)
        @JvmStatic fun bytes(name: String, commonName: String) = create<Bytes>(name, commonName)
        @JvmStatic fun chars(name: String, commonName: String) = create<Chars>(name, commonName)
        @JvmStatic fun data(name: String, commonName: String) = create<MutableData>(name, commonName)
        @JvmStatic fun <T> list(name: String, commonName: String) = create<MutableList<T>>(name, commonName)

        @JvmStatic fun <T: Any> shadow(name: String, commonName: String, real: Element<T>): Element<T> = ShadowElement(
            name,
            commonName,
            real
        )

        private inline fun <reified T: Any> create(
            name: String,
            commonName: String
        ): Element<T> = RealElement(
            name,
            commonName,
            T::class.java
        )

        fun contentsEquals(a: Data, b: Data): Boolean {
            val ka = a.keys()
            val kb = b.keys()

            if (ka.size != kb.size)
                return false

           return ka.all {
               val av = a[it]
               val bv = b[it]
               when {
                   av is ByteArray && bv is ByteArray -> av.contentEquals(bv)
                   av is CharArray && bv is CharArray -> av.contentEquals(bv)
                   else -> av == bv
               }
           }
        }
    }
}