package me.minhael.design.sl

import me.minhael.design.data.*
import org.nustaq.serialization.*
import java.io.InputStream
import java.io.OutputStream
import java.util.concurrent.locks.ReentrantLock

/**
 * Serializer using https://github.com/RuedigerMoeller/fast-serialization
 */
class FstSerializer(
    builder: () -> FSTConfiguration = { default() }
) : Serializer {

    private val conf: FSTConfiguration by lazy { builder() }
    private val lock = ReentrantLock(true)

    override fun serialize(obj: Any, outputStream: OutputStream) {
        lock.lock()
        try {
            conf.getObjectOutput(outputStream).apply {
                this.writeObject(obj, obj::class.java)
                this.flush()
            }
        } finally {
            lock.unlock()
        }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> deserialize(inputStream: InputStream, reference: TypeReference<T>): T {
        lock.lock()
        return try {
            conf.getObjectInput(inputStream).run {
                this.readObject(reference.asClass()) as T
            }
        } finally {
            lock.unlock()
        }
    }

    class DataSerializer(private val keyChain: KeyChain = KeyChain.DEFAULT) : FSTBasicObjectSerializer() {
        override fun writeObject(
            out: FSTObjectOutput,
            toWrite: Any,
            clzInfo: FSTClazzInfo,
            referencedBy: FSTClazzInfo.FSTFieldInfo,
            streamPosition: Int
        ) {
            val map = HashMap<String, Any>()
            MapData(map).apply { DataX.merge(this, toWrite as Data, Policy.FETCH, keyChain) }
            out.writeObject(map)
        }

        override fun instantiate(
            objectClass: Class<*>,
            `in`: FSTObjectInput,
            serializationInfo: FSTClazzInfo,
            referencee: FSTClazzInfo.FSTFieldInfo,
            streamPosition: Int
        ): Any {
            val obj = `in`.readObject()
            val data = MapData(klass.cast(obj))
            `in`.registerObject(data, streamPosition, serializationInfo, referencee)
            return data
        }

        companion object {
            private val klass = object : TypeReference<HashMap<String, Any>>() {}
        }
    }

    companion object {

        @JvmStatic fun default(conf: FSTConfiguration = FSTConfiguration.createDefaultConfiguration()) = conf.apply {
            registerSerializer(Data::class.java, DataSerializer(KeyChain.DEFAULT), true)
        }
    }
}