package me.minhael.design.db

import io.objectbox.BoxStore
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import me.minhael.design.test.DatabaseTest
import java.lang.IllegalArgumentException

@Entity
data class DOMEntity(
    @Id var id: Long = 0,
    override val uuid: String = "",
    override var time: Long = 0
) : DatabaseTest.DOM {

    val fkSet: ToOne<DOMSetEntity> = ToOne(this, DOMEntity_.fkSet)
    override var set: DatabaseTest.DOMSet
        get() = fkSet.target
        set(value) {
            if (value is DOMSetEntity)
                fkSet.target = value
            else
                throw IllegalArgumentException()
        }

    @JvmField
    @Transient
    var __boxStore: BoxStore? = null
}