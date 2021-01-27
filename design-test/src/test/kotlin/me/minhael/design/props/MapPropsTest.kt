package me.minhael.design.props

import me.minhael.design.test.PropsTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal open class MapPropsTest: PropsTest() {
    override val props = MapProps()
}