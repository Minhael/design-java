package me.minhael.design

import kotlin.reflect.KClass

/**
 * Service resolver. Context class to locate different services.
 */
class Sr {

    private val method = HashMap<Class<*>, () -> Any>()
    private val pool = HashMap<Class<*>, Any>()

    fun register(vararg klass: KClass<*>, obj: (Sr) -> Any) {
        register(*klass.map { it.javaObjectType }.toTypedArray()) { obj(it) }
    }

    fun register(vararg klass: Class<*>, obj: (Sr) -> Any) {
        klass.forEach { method[it] = { create(obj, *klass) } }
    }

    inline fun <reified T: Any> obtain() = obtain(T::class.javaObjectType)

    fun <T: Any> obtain(klass: Class<T>): T {
        return if (pool.containsKey(klass))
            klass.cast(pool.getValue(klass))
        else
            method[klass]?.invoke()?.let { klass.cast(it) } ?: throw IllegalArgumentException()
    }

    fun clear() {
        pool.clear()
    }

    private fun create(func: (Sr) -> Any, vararg klass: Class<*>): Any {
        val obj = func(this)

        //  Cache instance
        klass.forEach {
            if (it.isInstance(obj))
                pool[it] = obj
        }

        return obj
    }
}