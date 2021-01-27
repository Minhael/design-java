package me.minhael.design.test

import me.minhael.design.data.KeyChain
import me.minhael.design.sl.Serializer
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.Is.`is`
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

abstract class SerializerTest {

    abstract val serializer: Serializer

    protected fun testPrimitives() {
        TVals.vList().forEach { test(it) }
        test(TVals.vData())
        test(TVals.A())
    }

    protected fun testCollections() {
        test(TVals.vList())
        test(TVals.vMap())
        test(TVals.vComplexList())
        test(TVals.vComplexMap())
        test(TVals.vComplexCollection())
        test(TVals.B())
        test(TVals.C())
    }

    protected inline fun <reified T: Any> test(obj: T, assertion: (T, T) -> Unit = { ex, re -> assertEquals(ex, re) }) {
        val output = ByteArrayOutputStream().use {
            serializer.serialize(obj, it)
            it.toByteArray()
        }

        val result = ByteArrayInputStream(output).use {
            serializer.deserialize(it, obj::class.java)
        }

        assertion(obj, result)
    }

    companion object {
        @BeforeAll
        @JvmStatic
        fun init() {
            KeyChain.DEFAULT.include(
                TVals.KEY_BOOL,
                TVals.KEY_INT,
                TVals.KEY_LONG,
                TVals.KEY_BYTE,
                TVals.KEY_CHAR,
                TVals.KEY_BYTES,
                TVals.KEY_STR,
                TVals.KEY_CHARS,
                TVals.KEY_DATA,
                TVals.KEY_LIST
            )
        }
    }
}