package me.minhael.design.sl

import me.minhael.design.test.SerializerTest
import org.junit.jupiter.api.Test

internal class FstSerializerTest: SerializerTest {
    override val subject = FstSerializer()

    @Test
    fun testSerialize() {
        testPrimitives()
        testCollections()
    }
}