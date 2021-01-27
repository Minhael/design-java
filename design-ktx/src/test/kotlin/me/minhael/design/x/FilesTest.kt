package me.minhael.design.x

import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.File
import java.io.FileNotFoundException

internal class FilesTest {

    private val directory = File("deleteMe")
    private val file = File(directory, "WhatSoEver.txt")

    @BeforeEach
    fun setup() {
        file.delete()
        directory.deleteRecursively()
    }

    @AfterEach
    fun destroy() {
        file.delete()
        directory.deleteRecursively()
    }

    @Test
    fun touch() {
        assertFalse(file.exists())
        assertFalse(directory.exists())

        file.touch()

        assertTrue(directory.exists())
        assertTrue(directory.isDirectory)
        assertTrue(file.exists())
        assertTrue(file.isFile)
    }

    @Test
    fun parentNotDirectory() {
        assertFalse(file.exists())
        assertFalse(directory.exists())

        directory.createNewFile()

        assertTrue(directory.exists())
        assertTrue(directory.isFile)

        try {
            file.touch()
            fail()
        } catch (e: FileNotFoundException) {
            //  Success
        }
    }

    @Test
    fun fileNotFile() {
        assertFalse(file.exists())
        assertFalse(directory.exists())

        file.mkdirs()

        assertTrue(directory.exists())
        assertTrue(directory.isDirectory)
        assertTrue(file.exists())
        assertTrue(file.isDirectory)

        try {
            file.touch()
            fail()
        } catch (e: FileNotFoundException) {
            //  Success
        }
    }
}