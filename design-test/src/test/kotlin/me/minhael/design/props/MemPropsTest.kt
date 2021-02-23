package me.minhael.design.props

import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks

import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
class MemPropsTest {

    @Mock
    lateinit var props: Props

    @InjectMocks
    lateinit var subject: MemProps

    private val key = "key"
    private val key2 = "key2"
    private val key3 = "notexistingkey"

    @Test
    fun getPut() {
        testGetPut(subject)
    }

    private fun testGetPut(subject: Store) {
        //  Dependencies
        `when`(props.has(eq(key))).thenReturn(true)
        `when`(props.get<Long>(eq(key), any())).thenReturn(1L, 2L, 3L, 4L)
        `when`(props.has(key2)).thenReturn(false)
        `when`(props.has(key3)).thenReturn(false)

        /* Non existing data */
        `when`(props.get<Long>(eq(key3), any())).thenReturn(-1L)
        assertEquals(-1L, subject.get(key3, -1L))
        assertFalse(subject.has(key3))
        assertEquals(-1L, subject.get(key3, -1L))
        verify(props, times(3)).has(eq(key3))

        /* Existing data */
        assertTrue(subject.has(key))
        assertEquals(1L, subject.get(key, -1L))
        assertEquals(1L, subject.get(key, -1L))
        assertEquals(1L, subject.get(key, -1L))

        /* Put changes */
        val capture = argumentCaptor<Long>()
        subject.put(key, 5L)
        assertEquals(5L, subject.get(key, -1L))
        subject.put(key, 6L)
        assertEquals(6L, subject.get(key, -1L))
        subject.put(key, 11L)
        assertEquals(11L, subject.get(key, -1L))

        //  Write through
        verify(this.props, times(3)).put(eq(key), capture.capture())
        assertEquals(5L, capture.firstValue)
        assertEquals(6L, capture.secondValue)
        assertEquals(11L, capture.thirdValue)

        //  Put new key
        val newKey = "new"
        val value = ByteArray(7) { (it * 2).toByte() }
        subject.put(newKey, value)
        assertArrayEquals(value, subject.get(newKey, byteArrayOf()))

        //  Cached after init, no calls in changes
        verify(props, times(1)).get<Long>(eq(key), any())

        //  External changes
        `when`(props.get<String>(eq(key2), any())).thenReturn("-1")
        assertEquals("-1", subject.get(key2, "-1"))

        `when`(props.get<String>(eq(key2), any())).thenReturn("2", "3")
        //  Changed but not is called as the result should be cached, check by call times of has key2
//        `when`(props.has(eq(key2))).thenReturn(true)
        assertEquals("2", subject.get(key2, "-1"))
        assertEquals("2", subject.get(key2, "-1"))
        verify(props, times(1)).has(eq(key2))
        verify(props, times(2)).get<String>(eq(key2), any())
    }

    @Test
    fun has() {
        testHas(subject)
    }

    private fun testHas(subject: Store) {
        //  Dependencies
        `when`(props.has(eq(key))).thenReturn(true)
        `when`(props.get<Long>(eq(key), any())).thenReturn(1L, 2L, 3L, 4L)
        `when`(props.has(key3)).thenReturn(false)

        //  Non existing values
        assertFalse(subject.has(key3))
        assertFalse(subject.has(key3))
        assertFalse(subject.has(key3))

        //  Existing values
        assertTrue(subject.has(key))
        assertEquals(1L, subject.get(key, -1L))
        assertTrue(subject.has(key))

        //  Change after put
        subject.put(key, 5L)
        assertTrue(subject.has(key))
        assertEquals(5L, subject.get(key, -1L))
        assertTrue(subject.has(key))

        //  New value
        val newKey = "new"
        val value = ByteArray(7) { (it * 2).toByte() }
        assertFalse(subject.has(newKey))
        subject.put(newKey, value)
        assertTrue(subject.has(newKey))

        //  External changes
        assertFalse(subject.has(key2))
        `when`(props.get<String>(eq(key2), any())).thenReturn("1", "2", "3", "4")
        `when`(props.has(eq(key2))).thenReturn(true)
        assertTrue(subject.has(key2))
        assertEquals("1", subject.get(key2, "-1"))
        assertEquals("1", subject.get(key2, "-1"))
        assertTrue(subject.has(key2))
        verify(props, times(2)).has(eq(key2))
        verify(props, times(1)).get<String>(eq(key2), any())
    }

    @Test
    fun clear() {
        testClear(subject)
    }

    private fun testClear(subject: Store) {
        //  Dependencies
        `when`(props.has(eq(key))).thenReturn(true)
        `when`(props.get<Long>(eq(key), any())).thenReturn(1L, 2L, 3L, 4L)
        `when`(props.has(key2)).thenReturn(false)

        //  Non existing values
        assertFalse(subject.has(key2))

        //  Existing values
        assertTrue(subject.has(key))
        assertEquals(1L, subject.get(key, -1L))
        assertEquals(1L, subject.get(key, -1L))

        //  Cached after init
        verify(props, times(1)).get<Long>(eq(key), any())

        //  New values
        assertFalse(subject.has(key2))
        subject.put(key2, "1")
        assertTrue(subject.has(key2))
        assertEquals("1", subject.get(key2, "-1"))

        //  Clear
        subject.clear()
        verify(props).clear()
        `when`(props.get<Long>(eq(key2), any())).thenReturn(-1L)
        assertEquals(-1, subject.get(key2, -1L))
        assertFalse(subject.has(key2))
        assertTrue(subject.has(key))
        assertEquals(2, subject.get(key, -1L))
    }

    @Test
    fun commit() {
        subject.commit {
            testGetPut(it)
            testHas(it)
            testClear(it)
        }
    }

    @Test
    fun testCacheUpdateWithDefaultValue() {
        `when`(props.has(key2)).thenReturn(false)
        val cache = MemProps(props)

        assertFalse(cache.has(key2))
        `when`(props.get<Long>(eq(key2), any())).thenReturn(-1L)
        `when`(props.has(eq(key2))).thenReturn(true)
        assertEquals(-1L, cache.get(key2, -1L))
        assertTrue(cache.has(key2))
    }
}