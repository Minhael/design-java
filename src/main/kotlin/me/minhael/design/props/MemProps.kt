package me.minhael.design.props

import java.util.concurrent.ConcurrentHashMap

/**
 * Write-though cache for [Props] to avoid heavy read directly from persistence
 */
class MemProps(
        private val props: Props
) : Props {

    private val cache = ConcurrentHashMap<String, Any>()

    override fun <T : Any> put(key: String, value: T) {
        props.put(key, value)
        cache[key] = value
    }

    override fun <T : Any> get(key: String, defValue: T): T {
        return cache[key]
                ?.let {
                    defValue::class.java.cast(
                            when {
                                defValue is Long && it is Int -> it.toLong()
                                defValue is Int && it is Long -> it.toInt()
                                else -> it
                            }
                    )
                }
                ?: props.get(key, defValue).also {
                    if (it != defValue || props.has(key)) {
                        cache[key] = it
                    }
                }
    }

    override fun has(key: String): Boolean {
        return cache.containsKey(key) || props.has(key)
    }

    override fun clear() {
        cache.clear()
        props.clear()
    }

    override fun <T> commit(callable: (Store) -> T): T {
        return props.commit { callable(WrapperStore(cache, it)) }
    }

    private class WrapperStore(
            private val cache: MutableMap<String, Any>,
            private val store: Store
    ) : Store {

        override fun <T : Any> put(key: String, value: T) {
            store.put(key, value)
            cache[key] = value
        }

        override fun <T : Any> get(key: String, defValue: T): T {
            return cache[key]
                    ?.let {
                        defValue::class.java.cast(it)
                    }
                    ?: store.get(key, defValue).also {
                        if (it != defValue || store.has(key)) {
                            cache[key] = it
                        }
                    }
        }

        override fun has(key: String): Boolean {
            return cache.containsKey(key) || store.has(key)
        }

        override fun clear() {
            cache.clear()
            store.clear()
        }
    }
}