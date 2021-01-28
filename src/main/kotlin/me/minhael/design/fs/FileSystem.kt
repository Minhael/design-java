package me.minhael.design.fs

import java.io.File
import java.io.InputStream

interface FileSystem {

    /* Operations */
    fun create(mimeType: String, filename: String): String
    fun copy(inputStream: InputStream, mimeType: String, filename: String): String
    fun delete(filename: String): Boolean
    fun peek(uri: String): Meta
    fun browse(uri: String): FileSystem

    /* Information */
    fun root(): String
    fun space(): Space
    fun list(): List<String>
    fun listDir(): List<String>

    /* Helper */
    fun toFile(uri: String): File

    data class Meta(
        val uri: String,
        val filename: String?,
        val mimeType: String?,
        val size: Long
    )

    data class Space(
        val used: Long,
        val free: Long,
        val max: Long
    )
}