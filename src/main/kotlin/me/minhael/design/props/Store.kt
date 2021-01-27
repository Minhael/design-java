package me.minhael.design.props

/**
 * Interface to represent key-value store
 */
interface Store {

    fun <T : Any> put(key: String, value: T)
    fun <T : Any> get(key: String, defValue: T): T
    fun has(key: String): Boolean
    fun clear()
}