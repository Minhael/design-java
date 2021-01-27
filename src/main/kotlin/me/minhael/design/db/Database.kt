package me.minhael.design.db

import java.io.Closeable

/**
 * Interface to access database functions
 */
interface Database : Contracts, AutoCloseable, Closeable {

    /**
     * Run a transaction and return a value.
     *
     * Anything in callable is guaranteed to be in one piece, or reverted totally.
     *
     * @param callable function to manipulate DB
     * @param <T> value class returned
     * @return returned value
    </T> */
    fun <T> commit(callable: (Contracts) -> T): T

    /**
     * Run a read-only transaction and return a value.
     *
     * Anything in callable is guaranteed to be in one piece, or reverted totally.
     *
     * @param callable function to manipulate DB
     */
    fun <T> query(callable: (Contracts) -> T): T

    /**
     * Release & close the database connection
     */
    override fun close()
}