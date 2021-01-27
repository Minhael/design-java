package me.minhael.design.x

import me.minhael.design.misc.Klass
import kotlin.reflect.KClass

inline fun <reified T> Class<*>.isSuperClass() = Klass.isSuperClass(this, T::class.java)
inline fun <reified T> Class<*>.isSubClass() = Klass.isSubClass(this, T::class.java)
inline fun <reified T> KClass<*>.isSuperClass() = Klass.isSuperClass(this::class, T::class)
inline fun <reified T> KClass<*>.isSubClass() = Klass.isSubClass(this::class, T::class)