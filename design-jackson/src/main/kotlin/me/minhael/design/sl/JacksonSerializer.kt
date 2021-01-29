package me.minhael.design.sl

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.*
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import me.minhael.design.data.Data
import me.minhael.design.data.KeyChain
import me.minhael.design.data.MapData
import me.minhael.design.data.MutableData
import me.minhael.design.misc.Bytes
import me.minhael.design.misc.Chars
import java.io.InputStream
import java.io.OutputStream
import java.lang.reflect.Type

/**
 * Serializer using https://github.com/FasterXML/jackson-databind
 *
 * Please note that this serializer is only able to deserialize heterogeneous collections with Boolean, Int and String.
 * Please use [Data] otherwise.
 */
class JacksonSerializer(
    builder: () -> ObjectMapper = { default() }
) : Serializer {

    private val mapper: ObjectMapper by lazy { builder() }

    override fun serialize(obj: Any, outputStream: OutputStream) {
        return mapper.writeValue(outputStream, obj)
    }

    override fun <T> deserialize(inputStream: InputStream, reference: TypeReference<T>): T {
        return mapper.readValue(inputStream, object : com.fasterxml.jackson.core.type.TypeReference<T>() {
            override fun getType(): Type {
                return reference.asType()
            }
        })
    }

    class CharsSerializer : StdSerializer<Chars>(Chars::class.java) {
        override fun serialize(value: Chars, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeString(value.toString())
        }
    }

    class CharsDeserializer : StdDeserializer<Chars>(Chars::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Chars {
            return Chars(*p.valueAsString.toCharArray())
        }
    }

    class BytesSerializer : StdSerializer<Bytes>(Bytes::class.java) {
        override fun serialize(value: Bytes, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeBinary(value.toArray())
        }
    }

    class BytesDeserializer : StdDeserializer<Bytes>(Bytes::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): Bytes {
            return Bytes(*p.binaryValue)
        }
    }

    class DataSerializer(
        private val jc: JsonCodec = JsonCodec.DEFAULT
    ) : StdSerializer<Data>(Data::class.java) {
        override fun serialize(value: Data, gen: JsonGenerator, provider: SerializerProvider) {
            gen.writeTree(JsonData.toJson(value, gen.codec, jc))
        }
    }

    class DataDeserializer(
        private val keyChain: KeyChain = KeyChain.DEFAULT,
        private val jc: JsonCodec = JsonCodec.DEFAULT
    ) : StdDeserializer<MapData>(MapData::class.java) {
        override fun deserialize(p: JsonParser, ctxt: DeserializationContext): MapData {
            return MapData().apply {
                JsonData.read(p.codec.readValue(p, JsonNode::class.java), this, keyChain, jc)
            }
        }
    }

    companion object {

        @JvmStatic
        fun base(mapper: ObjectMapper = ObjectMapper()) = mapper
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)    //  No null value in output JSON
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)    //  Allow unknown properties in JSON
            .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)    //  Do not close the stream passing to mapper
            .registerModule(SimpleModule().apply {
                addSerializer(Chars::class.java, CharsSerializer())
                addDeserializer(Chars::class.java, CharsDeserializer())
                addSerializer(Bytes::class.java, BytesSerializer())
                addDeserializer(Bytes::class.java, BytesDeserializer())
            })

        @JvmStatic
        fun default(mapper: ObjectMapper = base()) = mapper
            .registerModule(SimpleModule().apply {
                addSerializer(DataSerializer())
                addDeserializer(Data::class.java, DataDeserializer())
                addDeserializer(MutableData::class.java, DataDeserializer())
                addDeserializer(MapData::class.java, DataDeserializer())
            })
    }
}