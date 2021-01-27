package me.minhael.design.fs

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType
import okio.BufferedSink
import java.io.IOException
import java.io.OutputStream

class HttpOctetOutputStream(client: OkHttpClient, url: String) : OutputStream() {

    private val scope = CoroutineScope(Dispatchers.IO)
    private val channel = Channel<Chunk>()
    private val result = Channel<Response>()

    private val body = object : RequestBody() {
        override fun contentType() = "application/octet-stream".toMediaType()

        override fun writeTo(sink: BufferedSink) {
            runBlocking {
                try {
                    while (true) {
                        channel.receive().apply {
                            scope.launch(Dispatchers.IO) {
                                sink.write(buffer, offset, length)
                            }
                        }
                    }
                } catch (e: ClosedReceiveChannelException) {
                    //  Finish
                }
            }
        }
    }

    private val callback = object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            scope.launch { result.close(e) }
        }

        override fun onResponse(call: Call, response: Response) {
            scope.launch { result.send(response) }
        }
    }

    init {
        val request = Request.Builder().url(url).post(body).build()
        client.newCall(request).enqueue(callback)
    }

    override fun write(b: ByteArray) {
        runBlocking { channel.send(Chunk(b, 0, b.size)) }
    }

    override fun write(b: ByteArray, off: Int, len: Int) {
        runBlocking { channel.send(Chunk(b, off, len)) }
    }

    override fun write(b: Int) {
        runBlocking { channel.send(Chunk(byteArrayOf((b and 0x00ff).toByte()), 0, 1)) }
    }

    override fun close() {
        super.close()
        runBlocking {
            channel.close()
            result.receive().also {
                if (!it.isSuccessful)
                    throw IOException("${it.code} ${it.message}")
            }
        }
    }

    private data class Chunk(
        val buffer: ByteArray,
        val offset: Int,
        val length: Int
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as Chunk

            if (!buffer.contentEquals(other.buffer)) return false
            if (offset != other.offset) return false
            if (length != other.length) return false

            return true
        }

        override fun hashCode(): Int {
            var result = buffer.contentHashCode()
            result = 31 * result + offset
            result = 31 * result + length
            return result
        }
    }
}