package me.minhael.design.x

import java.io.File
import java.io.FileNotFoundException

object Files {

    /**
     * Ensure the file is there.
     *
     * Create necessary parent directory & the file. Throw [FileNotFoundException] if parent is not directory or file is not [File]
     */
    fun touch(file: File): File {
        return file.apply {

            if (parentFile != null) {
                if (parentFile.exists() && parentFile.isFile)
                    throw FileNotFoundException("$parentFile is not a directory")

                if (!parentFile.exists())
                    parentFile.mkdirs()
            }

            if (exists() && isDirectory)
                throw FileNotFoundException("$file is not a file")

            if (!exists())
                createNewFile()
        }
    }
}

fun File.touch() = Files.touch(this)