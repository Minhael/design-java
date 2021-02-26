package me.minhael.design.state

import com.nhaarman.mockitokotlin2.argumentCaptor
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class ReduxTest {

    private val subject = Redux()

    @Test
    fun testDispatch() {
        val key = "Key1"
        val value1 = 1
        val value2 = 2
        val value3 = 3

        val observer = mock<(Int) -> Unit>()
        val handler = subject.observeWith(key, observer)

        subject.dispatch { mapOf(key to value1) }
        subject.dispatch { mapOf(key to value2) }
        subject.remove(key, handler)
        subject.dispatch { mapOf(key to value3) }

        val captor = argumentCaptor<Int>()
        verify(observer, times(2)).invoke(captor.capture())

        assertEquals(value1, captor.firstValue)
        assertEquals(value2, captor.secondValue)
    }

    @Test
    fun testSetState() {
        val key = "Key2"
        val value1 = 1
        val value2 = 1
        val value3 = 1

        val observer = mock<(Int) -> Unit>()
        val handler = subject.observeWith(key, observer)

        subject.setState { mapOf(key to value1) }
        subject.setState { mapOf(key to value2) }
        subject.remove(key, handler)
        subject.setState { mapOf(key to value3) }

        val captor = argumentCaptor<Int>()
        verify(observer, times(2)).invoke(captor.capture())

        assertEquals(value1, captor.firstValue)
        assertEquals(value2, captor.secondValue)
    }
}