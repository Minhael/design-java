package me.minhael.design.props

import me.minhael.design.sl.Serializer
import me.minhael.design.x.touch
import java.io.*
import java.util.*

/**
 * Implement [Props] using properties file
 */
class FileProps(
        private val file: File,
        private val serializer: Serializer
) : Props {

    override fun <T : Any> put(key: String, value: T) {
        load(file).also {
            setValue(serializer, it, key, value)
            save(file, it)
        }
    }

    override fun <T : Any> get(key: String, defValue: T): T {
        return getValue(serializer, load(file), key, defValue)
    }

    override fun has(key: String): Boolean {
        return load(file).getProperty(key) != null
    }

    override fun clear() {
        load(file).also {
            it.clear()
            save(file, it)
        }
    }

    override fun <T> commit(callable: (Store) -> T): T {
        val prop = load(file)
        val rt = callable(SessionProps(prop, serializer))
        save(file, prop)
        return rt
    }

    private class SessionProps(
            private val prop: Properties,
            private val serializer: Serializer
    ) : Store {

        override fun <T : Any> put(key: String, value: T) {
            setValue(serializer, prop, key, value)
        }

        override fun <T : Any> get(key: String, defValue: T): T {
            return getValue(serializer, prop, key, defValue)
        }

        override fun has(key: String): Boolean {
            return prop.getProperty(key) != null
        }

        override fun clear() {
            prop.clear()
        }
    }

    companion object {

        private fun <T : Any> setValue(serializer: Serializer, prop: Properties, key: String, value: T): T {
            return value
                    .also {
                        ByteArrayOutputStream().use {
                            serializer.serialize(value, it)
                            it.toByteArray()
                        }.run {
                            prop.setProperty(key, this.base64())
                        }
                    }
        }

        private fun <T : Any> getValue(serializer: Serializer, prop: Properties, key: String, defValue: T): T {
            return prop.getProperty(key)
                    ?.run {
                        ByteArrayInputStream(this.base64()).use {
                            serializer.deserialize(it, defValue::class.java)
                        }
                    }
                    ?: setValue(serializer, prop, key, defValue)
        }

        private fun load(file: File): Properties {
            synchronized(file) {
                return FileInputStream(file.touch()).use {
                    Properties().apply { this.load(it) }
                }
            }
        }

        private fun save(file: File, prop: Properties) {
            synchronized(file) {
                FileOutputStream(file).use {
                    prop.store(it, System.currentTimeMillis().toString())
                }
            }
        }
    }
}