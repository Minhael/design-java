package me.minhael.design.x

import io.objectbox.Box
import io.objectbox.query.Query
import io.objectbox.query.QueryBuilder

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

inline fun <T> Box<T>.query(function: (QueryBuilder<T>) -> Query<T> = { it.build() }): Query<T> {
    return this.query().use(function)
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