package me.minhael.design.db

/**
 * Gateway to register & access DAO contracts
 */
interface Contracts {

    /**
     * Get a pre-defined contract
     *
     * @param daoClass Class of that contract class
     * @param <T> Class implemented logic manipulating entities
     * @return T
    </T> */
    fun <T : Any> of(daoClass: Class<T>): T?
}