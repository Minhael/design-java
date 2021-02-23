package me.minhael.design.db

import io.objectbox.Box
import io.objectbox.BoxStore
import me.minhael.design.test.DatabaseTest
import me.minhael.design.x.objectboxBuilder
import me.minhael.design.x.query

class DOMSetDaoImpl(
    private val boxStore: BoxStore,
    private val box: Box<DOMSetEntity>
): DatabaseTest.DOMSetDao {

    override fun insert(set: DatabaseTest.DOMSet): DatabaseTest.DOMSet {
        return DOMSetEntity(0, set.uuid).also { box.put(it) }
    }

    override fun query(uuid: String): List<DatabaseTest.DOMSet> {
        return box
            .query { it.equal(DOMSetEntity_.uuid, uuid) }
            .use { it.find() }
    }

    override fun remove(vararg set: DatabaseTest.DOMSet): Long {
        val uuid = set.map { it.uuid }.toTypedArray()
        return box
            .query { it.`in`(DOMSetEntity_.uuid, uuid) }
            .use { it.remove() }
    }

    companion object {
        val builder = objectboxBuilder<DatabaseTest.DOMSetDao, DOMSetEntity> {
                boxStore, box -> DOMSetDaoImpl(boxStore, box)
        }
    }
}