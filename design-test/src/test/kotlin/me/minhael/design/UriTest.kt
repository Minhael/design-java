package me.minhael.design

import me.minhael.design.fs.Uri
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import java.io.InputStream
import java.io.OutputStream

internal class UriTest {

    @Mock
    lateinit var accessor: Uri.Accessor

    @Mock
    lateinit var inputStream: InputStream

    @Mock
    lateinit var outputStream: OutputStream

    lateinit var resolver: Uri.Resolver

    @BeforeEach
    fun setup() {
        MockitoAnnotations.initMocks(this)

        `when`(accessor.schemes).thenReturn(listOf("MEM"))
        `when`(accessor.readFrom("mem:test")).thenReturn(inputStream)
        `when`(accessor.writeTo("mem:test")).thenReturn(outputStream)

        resolver = Uri.Resolver(accessor)
    }

    @Test
    fun readFrom() {
        assertEquals(inputStream, resolver.readFrom("mem:test"))
    }

    @Test
    fun readInvalidUri() {
        try {
            resolver.readFrom("123das09he1da")
            fail()
        } catch (e: UnsupportedOperationException) {
            //  Expected
        }
    }

    @Test
    fun readNoSuchAccessor() {
        try {
            resolver.readFrom("http://localhost")
            fail()
        } catch (e: UnsupportedOperationException) {
            //  Expected
        }
    }

    @Test
    fun writeTo() {
        assertEquals(outputStream, resolver.writeTo("mem:test"))
    }

    @Test
    fun writeInvalidUri() {
        try {
            resolver.writeTo("123das09he1da")
            fail()
        } catch (e: UnsupportedOperationException) {
            //  Expected
        }
    }

    @Test
    fun writeNoSuchAccessor() {
        try {
            resolver.writeTo("http://localhost")
            fail()
        } catch (e: UnsupportedOperationException) {
            //  Expected
        }
    }
}