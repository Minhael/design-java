package me.minhael.design.db

import io.objectbox.Box
import io.objectbox.BoxStore
import me.minhael.design.test.DatabaseTest
import me.minhael.design.x.objectboxBuilder
import me.minhael.design.x.query

class DOMDaoImpl(
    private val boxStore: BoxStore,
    private val box: Box<DOMEntity>
): DatabaseTest.DOMDao {

    override fun insert(dom: DatabaseTest.DOM): DatabaseTest.DOM {
        return DOMEntity(0, dom.uuid, dom.time).apply { set = dom.set }.also { box.put(it) }
    }

    override fun query(uuid: String): List<DatabaseTest.DOM> {
        return box
            .query { it.equal(DOMEntity_.uuid, uuid) }
            .use { it.find() }
    }

    override fun remove(vararg dom: DatabaseTest.DOM): Long {
        val uuid = dom.map { it.uuid }.toTypedArray()
        return box
            .query { it.`in`(DOMEntity_.uuid, uuid) }
            .use { it.remove() }
    }

    companion object {
        val builder = objectboxBuilder<DatabaseTest.DOMDao, DOMEntity> {
                boxStore, box -> DOMDaoImpl(boxStore, box)
        }
    }
}