package me.minhael.design.misc;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class KlassJavaTest {

    class A { }
    class B extends A { }
    class C extends B { }

    @Test
    void testIsSuper() {
        assertTrue(Klass.isSuperClass(A.class, B.class));
        assertTrue(Klass.isSuperClass(A.class, C.class));
        assertTrue(Klass.isSuperClass(B.class, C.class));

        assertFalse(Klass.isSuperClass(B.class, A.class));
        assertFalse(Klass.isSuperClass(C.class, A.class));
        assertFalse(Klass.isSuperClass(C.class, B.class));

        assertTrue(Klass.isSuperClass(int.class, Integer.class));
        assertTrue(Klass.isSuperClass(Integer.class, int.class));
    }

    @Test
    void testIsSub() {
        assertFalse(Klass.isSubClass(A.class, B.class));
        assertFalse(Klass.isSubClass(A.class, C.class));
        assertFalse(Klass.isSubClass(B.class, C.class));

        assertTrue(Klass.isSubClass(B.class, A.class));
        assertTrue(Klass.isSubClass(C.class, A.class));
        assertTrue(Klass.isSubClass(C.class, B.class));

        assertTrue(Klass.isSubClass(int.class, Integer.class));
        assertTrue(Klass.isSubClass(Integer.class, int.class));
    }
}
