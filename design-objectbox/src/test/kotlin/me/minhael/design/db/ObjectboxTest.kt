package me.minhael.design.db

import me.minhael.design.test.DatabaseTest
import org.junit.jupiter.api.Test
import java.io.File

class ObjectboxTest: DatabaseTest {

    override fun open(): Database {
        val boxStore = MyObjectBox.builder()
            .baseDirectory(File("${System.getProperty("user.dir")}/out")).name("database")
            .build()
        return Objectbox(boxStore, DOMDaoImpl.builder, DOMSetDaoImpl.builder)
    }
}