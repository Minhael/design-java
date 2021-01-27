package me.minhael.design.db

import io.objectbox.BoxStore

/**
 * Database framework using https://objectbox.io
 *
 * If this is used in plain Kotlin,
 * make sure to check https://github.com/objectbox/objectbox-java/issues/440
 * before creating entities
 */
class Objectbox(
        private val boxStore: BoxStore,
        private vararg val builders: DaoBuilder<*>
) : Database {

    override fun <T : Any> of(daoClass: Class<T>) = builders
            .find { daoClass.isAssignableFrom(it.entityClass()) }
            ?.build(boxStore)
            ?.let { daoClass.cast(it) }
            ?: throw IllegalArgumentException("${daoClass.simpleName} is not registered in this database")

    override fun <T : Any?> commit(callable: (Contracts) -> T) = try {
        boxStore.callInTx { callable(this) }
    } catch (e: RuntimeException) {
        throw e.cause ?: e
    } finally {
        boxStore.closeThreadResources()
    }

    override fun <T : Any?> query(callable: (Contracts) -> T) = try {
        boxStore.callInReadTx { callable(this@Objectbox) }
    } catch (e: RuntimeException) {
        throw e.cause ?: e
    } finally {
        boxStore.closeThreadResources()
    }

    override fun close() {}

    interface DaoBuilder<DAO : Any> {
        fun entityClass(): Class<DAO>
        fun build(s: BoxStore): DAO
    }
}