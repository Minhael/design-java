package me.minhael.design.db

import io.objectbox.BoxStore
import io.objectbox.annotation.Backlink
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToMany
import me.minhael.design.test.DatabaseTest

@Entity
data class DOMSetEntity(
    @Id var id: Long = 0,
    override val uuid: String = ""
) : DatabaseTest.DOMSet {

    @Backlink
    override val dom: ToMany<DOMEntity> = ToMany(this, DOMSetEntity_.dom)

    @JvmField
    @Transient
    var __boxStore: BoxStore? = null
}