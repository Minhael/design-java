package me.minhael.design.props

import me.minhael.design.test.PropsTest
import me.minhael.design.sl.FstSerializer
import java.io.File

internal class FilePropsTest : PropsTest {
    private val file = File(javaClass.classLoader.getResource("prop.properties").file)
    override val subject = FileProps(file, FstSerializer())
}