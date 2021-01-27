package me.minhael.design.data

import me.minhael.design.test.TVals
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException

internal class KeyChainTest {

    @Test
    fun test() {
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
        //  Same keys are ignored
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
        try {
            KeyChain.DEFAULT.include(duplicated)
            fail("Should throw exception")
        } catch (e: IllegalArgumentException) {
            //  success
        }
        try {
            KeyChain.DEFAULT.include(duplicated)
            fail("Should throw exception")
        } catch (e: IllegalArgumentException) {
            //  success
        }
    }

    private val duplicated = Data.boolean("bool", "should be error")
}