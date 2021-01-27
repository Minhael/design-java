package me.minhael.design.sl

import me.minhael.design.test.SerializerTest
import me.minhael.design.test.TVals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.io.Serializable

internal class JacksonSerializerTest: SerializerTest() {
    override val serializer = JacksonSerializer { JacksonSerializer.default() }

    @Test
    fun testSerialize() {
        testPrimitives()
        test(jList)
        test(jMap)
        test(jComplexList)
        test(jComplexMap)
        test(jComplexCollections)
        test(B())
        test(C())
    }

    open class B(
        val list: List<Any> = jList,
        val map: Map<String, Any> = jMap,
    ): Serializable {

        override fun hashCode(): Int {
            var result = list.hashCode()
            result = 31 * result + map.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as B

            if (list != other.list) return false
            if (map != other.map) return false

            return true
        }
    }

    class C(val a: TVals.A = TVals.A()): B(), Serializable {

        override fun hashCode(): Int {
            var result = super.hashCode()
            result = 31 * result + a.hashCode()
            return result
        }

        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false
            if (!super.equals(other)) return false

            other as C

            if (a != other.a) return false

            return true
        }
    }

    companion object {

        @BeforeAll
        fun init() {
            JsonCodec.DEFAULT.register(TVals.KEY_LIST, Codec.ListCodec(TVals.KEY_DATA))
        }

        private val jList = listOf(TVals.vBool, TVals.vInt, TVals.vStr)
        private val jMap = mapOf(
            TVals.KEY_BOOL.name to TVals.vBool,
            TVals.KEY_INT.name to TVals.vInt,
            TVals.KEY_STR.name to TVals.vStr
        )
        private val jComplexList = listOf(jMap, jMap, jMap)
        private val jComplexMap = mapOf(
            "a" to jList,
            "b" to jList,
            "c" to jList
        )
        private val jComplexCollections = listOf(
            mapOf(
                "a" to jComplexList,
                "b" to jComplexList,
                "c" to jComplexList
            ),
            mapOf(
                "a" to jComplexList,
                "b" to jComplexList,
                "c" to jComplexList
            )
        )
    }
}