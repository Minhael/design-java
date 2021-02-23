package me.minhael.design.fs

import java.io.File
import java.io.InputStream

interface FileSystem {

    /* File Operations */
    fun list(): List<String>
    fun create(mimeType: String, filename: String): String
    fun copy(inputStream: InputStream, mimeType: String, filename: String): String
    fun find(filename: String): Meta?
    fun peek(uri: String): Meta?
    fun delete(uri: String): Boolean

    /* Directory Operations */
    fun createDir(dirname: String): FileSystem
    fun listDir(): List<String>
    fun browse(uri: String): FileSystem?
    fun deleteDir(uri: String): Boolean
    fun destroy(): Boolean

    /* Information */
    fun root(): String
    fun space(): Space

    /* Helper */
    fun toFile(uri: String): File
    fun accessor(): Uri.Accessor

    data class Meta(
        val uri: String,
        val filename: String,
        val mimeType: String,
        val size: Long
    )

    data class Space(
        val used: Long,
        val free: Long,
        val max: Long
    )
}