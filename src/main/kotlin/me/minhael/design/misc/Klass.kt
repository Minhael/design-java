package me.minhael.design.misc

import kotlin.reflect.KClass

object Klass {

    @JvmStatic fun isSuperClass(me: Class<*>, target: Class<*>): Boolean {
        return isSuperClass(me.kotlin, target.kotlin)
    }

    @JvmStatic fun isSubClass(me: Class<*>, target: Class<*>): Boolean {
        return isSubClass(me.kotlin, target.kotlin)
    }

    fun isSuperClass(me: KClass<*>, target: KClass<*>): Boolean {
        return me.javaObjectType.isAssignableFrom(target.javaObjectType)
    }

    fun isSubClass(me: KClass<*>, target: KClass<*>): Boolean {
        return target.javaObjectType.isAssignableFrom(me.javaObjectType)
    }
}