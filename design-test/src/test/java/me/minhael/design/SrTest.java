package me.minhael.design;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SrTest {

    private final Sr sr = new Sr();

    @Test
    void test() {
        sr.register(new Class[]{ Integer.class }, sr -> 2);
        assertEquals(2, sr.obtain(Integer.class));

        sr.register(new Class[]{ Integer.class}, sr -> 3);

        assertEquals(2, sr.obtain(Integer.class));
        sr.clear();
        assertEquals(3, sr.obtain(Integer.class));
    }
}
