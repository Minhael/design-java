package me.minhael.design.test

import me.minhael.design.db.Database
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import java.util.*

interface DatabaseTest {

    fun open(): Database

    interface DOM {
        val uuid: String
        var time: Long
        val set: DOMSet
    }

    interface DOMDao {
        fun insert(dom: DOM): DOM
        fun query(uuid: String): List<DOM>
        fun remove(vararg dom: DOM): Long
    }

    data class DOMInstance(
        override val uuid: String,
        override val set: DOMSet,
        override var time: Long
    ): DOM

    interface DOMSet {
        val uuid: String
        val dom: List<DOM>
    }

    interface DOMSetDao {
        fun insert(set: DOMSet): DOMSet
        fun query(uuid: String): List<DOMSet>
        fun remove(vararg set: DOMSet): Long
    }

    data class DOMSetInstance(
        override val uuid: String,
        override val dom: List<DOM> = emptyList()
    ): DOMSet

    @Test
    fun testNormalIO() {
        val setID1 = UUID.randomUUID().toString()
        val id1 = UUID.randomUUID().toString()
        val time = System.currentTimeMillis()

        //  Insert testing data
        open().use {
            it.commit { subject ->
                val dao = subject.of(DOMDao::class.java) ?: fail("Failed to get DOM DAO")
                val setDao = subject.of(DOMSetDao::class.java) ?: fail("Failed to get DOM set DAO")
                val set = setDao.insert(DOMSetInstance(setID1))
                val obj = dao.insert(DOMInstance(id1, set, time))
            }
        }

        open().use { subject ->
            //  Check query & relationship
            val dao = subject.of(DOMDao::class.java) ?: fail("Failed to get DOM DAO")
            val result = dao.query(id1)
            assertEquals(1, result.size)
            assertEquals(id1, result[0].uuid)
            assertEquals(time, result[0].time)

            val setDao = subject.of(DOMSetDao::class.java) ?: fail("Failed to get DOM set DAO")
            val resultSet = setDao.query(setID1)
            assertEquals(1, resultSet.size)
            assertEquals(setID1, resultSet[0].uuid)

            assertEquals(resultSet[0], result[0].set)
            assertEquals(listOf(result[0]), resultSet[0].dom)
        }

        open().use { subject ->
            //  Check query & relationship
            val dao = subject.of(DOMDao::class.java) ?: fail("Failed to get DOM DAO")
            val result = dao.query(id1)
            dao.remove(*result.toTypedArray())

            val setDao = subject.of(DOMSetDao::class.java) ?: fail("Failed to get DOM set DAO")
            val resultSet = setDao.query(setID1)
            setDao.remove(*resultSet.toTypedArray())

            assertEquals(0, dao.query(id1).size)
            assertEquals(0, setDao.query(setID1).size)
        }
    }

    @Test
    fun testCommitInterrupt() {
        val setID1 = UUID.randomUUID().toString()
        val id1 = UUID.randomUUID().toString()
        val time = System.currentTimeMillis()

        try {
            open().use {
                it.commit { subject ->
                    val dao = subject.of(DOMDao::class.java) ?: fail("Failed to get DOM DAO")
                    val setDao = subject.of(DOMSetDao::class.java) ?: fail("Failed to get DOM set DAO")
                    val set = setDao.insert(DOMSetInstance(setID1))
                    val obj = dao.insert(DOMInstance(id1, set, time))
                    throw IntentionalError()
                }
            }
            fail("IntentionalError should be thrown from database")
        } catch (e: IntentionalError) {
            open().use { subject ->
                //  Check query & relationship
                val dao = subject.of(DOMDao::class.java) ?: fail("Failed to get DOM DAO")
                val setDao = subject.of(DOMSetDao::class.java) ?: fail("Failed to get DOM set DAO")
                assertEquals(0, dao.query(id1).size)
                assertEquals(0, setDao.query(setID1).size)
            }
        }
    }

    private class IntentionalError: Throwable()
}