package me.minhael.design.misc

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class KlassKotlinTest {

    open class A
    open class B : A()
    class C : B()

    @Test
    fun testIsSuper() {
        assertTrue(Klass.isSuperClass(A::class, B::class))
        assertTrue(Klass.isSuperClass(A::class, C::class))
        assertTrue(Klass.isSuperClass(B::class, C::class))

        assertFalse(Klass.isSuperClass(B::class, A::class))
        assertFalse(Klass.isSuperClass(C::class, A::class))
        assertFalse(Klass.isSuperClass(C::class, B::class))

        val i = 2
        assertTrue(Klass.isSuperClass(Int::class, i::class))
        assertTrue(Klass.isSuperClass(i::class, Int::class))

        assertTrue(Klass.isSuperClass(Int::class, Integer::class))
        assertTrue(Klass.isSuperClass(Integer::class, Int::class))
    }

    @Test
    fun testIsSub() {
        assertFalse(Klass.isSubClass(A::class, B::class))
        assertFalse(Klass.isSubClass(A::class, C::class))
        assertFalse(Klass.isSubClass(B::class, C::class))

        assertTrue(Klass.isSubClass(B::class, A::class))
        assertTrue(Klass.isSubClass(C::class, A::class))
        assertTrue(Klass.isSubClass(C::class, B::class))

        val i = 2
        assertTrue(Klass.isSubClass(Int::class, i::class))
        assertTrue(Klass.isSubClass(i::class, Int::class))

        assertTrue(Klass.isSubClass(Int::class, Integer::class))
        assertTrue(Klass.isSubClass(Integer::class, Int::class))
    }
}