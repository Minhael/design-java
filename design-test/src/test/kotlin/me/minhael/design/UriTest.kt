package me.minhael.design

import me.minhael.design.fs.Uri
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import java.io.InputStream
import java.io.OutputStream

@ExtendWith(MockitoExtension::class)
internal class UriTest {

    @Mock
    lateinit var accessor: Uri.Accessor

    @Mock
    lateinit var inputStream: InputStream

    @Mock
    lateinit var outputStream: OutputStream

    private val subject: Uri.Resolver by lazy {
        Uri.Resolver(accessor)
    }

    @BeforeEach
    fun setup() {
        `when`(accessor.schemes).thenReturn(listOf("MEM"))
    }

    @Test
    fun readFrom() {
        `when`(accessor.readFrom("mem:test")).thenReturn(inputStream)

        assertEquals(inputStream, subject.readFrom("mem:test"))
    }

    @Test
    fun readInvalidUri() {
        try {
            subject.readFrom("123das09he1da")
            fail()
        } catch (e: UnsupportedOperationException) {
            //  Expected
        }
    }

    @Test
    fun readNoSuchAccessor() {
        try {
            subject.readFrom("http://localhost")
            fail()
        } catch (e: UnsupportedOperationException) {
            //  Expected
        }
    }

    @Test
    fun writeTo() {
        `when`(accessor.writeTo("mem:test")).thenReturn(outputStream)

        assertEquals(outputStream, subject.writeTo("mem:test"))
    }

    @Test
    fun writeInvalidUri() {
        try {
            subject.writeTo("123das09he1da")
            fail()
        } catch (e: UnsupportedOperationException) {
            //  Expected
        }
    }

    @Test
    fun writeNoSuchAccessor() {
        try {
            subject.writeTo("http://localhost")
            fail()
        } catch (e: UnsupportedOperationException) {
            //  Expected
        }
    }
}