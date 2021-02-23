package me.minhael.design.x

import io.objectbox.Box
import io.objectbox.BoxStore
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder
import me.minhael.design.db.Objectbox

inline fun <reified DAO: Any, reified ENTITY> objectboxBuilder(crossinline constructor: (BoxStore, Box<ENTITY>) -> DAO) = object : Objectbox.DaoBuilder<DAO> {
    override fun entityClass() = DAO::class.java
    override fun build(s: BoxStore) = constructor(s, s.boxFor(ENTITY::class.java))
}

fun <T> Query<T>.findAt(index: Int): T? {
    val list = this.find()
    return if (list.isEmpty())
        null
    else
        list[index]
}

inline fun <T, R> QueryBuilder<T>.use(function: (QueryBuilder<T>) -> R): R {
    try {
        return function.invoke(this)
    } finally {
        this.close()
    }
}

inline fun <T, R> Query<T>.use(function: (Query<T>) -> R): R {
    try {
        return function.invoke(this)
    } finally {
        this.close()
    }
}

inline fun <T> Box<T>.query(function: (QueryBuilder<T>) -> QueryBuilder<T> = { it }): Query<T> {
    return this.query().use { function(it).build() }
}

inline fun <T, R> QueryBuilder<T>.find(function: (Query<T>) -> R): R {
    return this.use {
        it.build()
    }.use {
        function.invoke(it)
    }
}

inline fun <T, R> Box<T>.find(function: (Query<T>) -> R): R {
    return this.query().find(function)
}