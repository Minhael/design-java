package me.minhael.design

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SrKotlinTest {

    private val sr = Sr()

    @Test
    fun test() {
        sr.register(Int::class) { 2 }
        assertEquals(2, sr.obtain())

        sr.register(Int::class) { 3 }

        assertEquals(2, sr.obtain())
        sr.clear()
        assertEquals(3, sr.obtain())
    }
}