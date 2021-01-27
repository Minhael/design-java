package me.minhael.design.fs

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

/**
 * Access to HTTP resources using OkHttp library
 */
class OkUriAccessor(
    private val builder: () -> OkHttpClient = { OkHttpClient.Builder().build() }
) : Uri.Accessor {

    override val schemes = listOf("http", "https")

    override fun readFrom(uri: String): InputStream {
        return builder()
            .newCall(Request.Builder().url(uri).build())
            .execute()
            .let {
                if (it.isSuccessful)
                    it.body?.byteStream() ?: throw IOException("Empty body")
                else
                    throw IOException("${it.code} ${it.message}")
            }
    }

    override fun writeTo(uri: String): OutputStream {
        return HttpOctetOutputStream(builder(), uri)
    }
}