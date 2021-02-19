package me.minhael.design.test

import me.minhael.design.fs.FileSystem
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestFactory

abstract class FileSystemTest {

    abstract fun new(): FileSystem

    lateinit var subject: FileSystem

    @BeforeEach
    fun setup() {
        subject = new()
    }

    @AfterEach
    fun tearDown() {
        subject.destroy()
    }

    open val files = listOf(
        FileSystem.Meta("", "file1.txt", "text/plain", 272L) to "Quick Brown Fox Jump Over Lazy Dog",
        FileSystem.Meta("", "file2.txt", "text/plain", 96L) to "Hello World!"
    )

    @Test
    fun testFilesNegative() {
        assertEquals(0, subject.list().size)
        assertNull(subject.peek(""))
        assertTrue(subject.delete(""))
    }

    @Test
    fun testFiles() {
        //  Input
        val (meta1, content1) = files[0]
        val (meta2, content2) = files[1]

        //  First file
        val uri1 = subject.create(meta1.mimeType, meta1.filename)
        subject.accessor().writeTo(uri1).use {
            it.bufferedWriter().apply {
                append(content1)
                flush()
            }
        }
        subject.accessor().readFrom(uri1).use {
            val result = it.reader().readText()
            assertEquals(content1, result)
        }
        assertEquals(meta1.copy(uri = uri1), subject.peek(uri1))

        //  Second file
        val uri2 = subject.create(meta2.mimeType, meta2.filename)
        subject.accessor().writeTo(uri2).use {
            it.bufferedWriter().apply {
                append(content2)
                flush()
            }
        }
        assertEquals(meta2.copy(uri = uri2), subject.peek(uri2))

        //  Copy file
        val copied = "copied.txt"
        val uri3 = subject.accessor().readFrom(uri2).use {
            subject.copy(it, meta2.mimeType, copied)
        }
        subject.accessor().readFrom(uri3).use {
            assertEquals(content2, it.reader().readText())
        }
        assertEquals(meta2.copy(uri = uri3, filename = copied), subject.peek(uri3))

        //  Assert list
        val allFiles = listOf(uri1, uri2, uri3)
        assertEquals(allFiles, subject.list())

        //  Delete a file
        assertTrue(subject.delete(uri2))
        assertEquals(listOf(uri1, uri3), subject.list())

        //  Negative peek
        assertNull(subject.peek(uri2))

        //  Negative delete
        assertTrue(subject.delete(uri2))
    }

    @Test
    fun testDirNegative() {
        assertEquals(0, subject.listDir().size)
        assertNull(subject.browse(""))
        assertTrue(subject.deleteDir(""))
    }

    @Test
    fun testDirs() {
        //  Input
        val (meta1, content1) = files[0]
        val (meta2, content2) = files[1]

        //  Create sub dirs
        val fs1 = subject.createDir("sub1")
        val fs2 = subject.createDir("sub2")
        val fs3 = subject.createDir("sub3")
        val fs4 = subject.createDir("sub4")

        //  List dirs
        val dirs = subject.listDir()
        assertEquals(listOf(fs1.root(), fs2.root(), fs3.root(), fs4.root()), dirs)

        //  Delete empty dir
        assertTrue(subject.deleteDir(dirs[0]))
        assertTrue(fs2.destroy())
        assertNull(subject.browse(dirs[0]))
        assertNull(subject.browse(dirs[1]))

        //  List dirs again
        val dirs2 = subject.listDir()
        assertEquals(listOf(fs3.root(), fs4.root()), dirs2)

        //  Locate dir
        val sub3 = subject.browse(dirs2[0]) ?: fail()
        val sub4 = subject.browse(dirs2[1]) ?: fail()

        //  Write sth into the dir
        val uri1 = sub3.create(meta1.mimeType, meta1.filename)
        subject.accessor().writeTo(uri1).use {
            it.bufferedWriter().apply {
                append(content1)
                flush()
            }
        }

        val uri2 = sub4.create(meta2.mimeType, meta2.filename)
        subject.accessor().writeTo(uri2).use {
            it.bufferedWriter().apply {
                append(content2)
                flush()
            }
        }

        //  Locate file from root
        subject.browse(subject.listDir()[0])
            ?.let { it.accessor().readFrom(it.list()[0]) }
            ?.use { assertEquals(content1, it.reader().readText()) }
            ?: fail()

        //  Delete filled dir
        assertTrue(subject.deleteDir(dirs2[0]))
        assertTrue(fs4.destroy())
        assertNull(subject.browse(dirs2[0]))
        assertNull(subject.browse(dirs2[1]))

        assertEquals(0, subject.listDir().size)
    }
}