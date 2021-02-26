package me.minhael.design.x

inline fun <reified T> Map<String, Any>.getWith(name: String, defValue: T) = T::class.java.cast(getOrElse(name) { defValue })