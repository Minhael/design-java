package me.minhael.design.test

import me.minhael.design.data.KeyChain
import me.minhael.design.sl.Serializer
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

interface SerializerTest {

    val subject: Serializer

    fun testPrimitives() {
        TVals.vList().forEach { test(subject, it) }
        test(subject, TVals.vData())
        test(subject, TVals.A())
    }

    fun testCollections() {
        test(subject, TVals.vList())
        test(subject, TVals.vMap())
        test(subject, TVals.vComplexList())
        test(subject, TVals.vComplexMap())
        test(subject, TVals.vComplexCollection())
        test(subject, TVals.B())
        test(subject, TVals.C())
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

        inline fun <reified T: Any> test(serializer: Serializer, obj: T) {
            val output = ByteArrayOutputStream().use {
                serializer.serialize(obj, it)
                it.toByteArray()
            }

            val result = ByteArrayInputStream(output).use {
                serializer.deserialize(it, obj::class.java)
            }

            assertEquals(obj, result)
        }
    }
}