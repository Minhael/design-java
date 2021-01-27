package me.minhael.design.props

import me.minhael.design.test.PropsTest
import me.minhael.design.sl.FstSerializer
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.lang.RuntimeException

internal class FilePropsTest : PropsTest() {
    private val file = File(javaClass.classLoader.getResource("prop.properties").file)
    override val props = FileProps(file, FstSerializer())
}