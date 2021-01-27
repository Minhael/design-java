package me.minhael.design.data

interface MutableData : Data {

    operator fun <T: Any> set(key: Data.Element<T>, value: T): T?
    fun <T: Any> remove(key: Data.Element<T>): T?
    fun clear()
}