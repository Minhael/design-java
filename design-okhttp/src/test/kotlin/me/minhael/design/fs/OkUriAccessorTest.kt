package me.minhael.design.fs

import com.jayway.jsonpath.internal.path.PathCompiler.fail
import io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE
import me.minhael.design.test.TVals
import org.apache.commons.io.IOUtils
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockserver.integration.ClientAndServer
import org.mockserver.model.Header
import org.mockserver.model.HttpError
import org.mockserver.model.HttpRequest.request
import org.mockserver.model.HttpResponse.response
import org.mockserver.model.HttpStatusCode
import org.mockserver.model.MediaType
import org.mockserver.verify.VerificationTimes.exactly
import java.io.IOException

internal class OkUriAccessorTest {

    private val accessor = OkUriAccessor()

    @BeforeEach
    fun setup() {
        server.reset()
    }

    @Test
    fun testSchemes() {
        assertEquals(listOf("http", "https"), accessor.schemes)
    }

    @Test
    fun readFrom() {
        server
            .`when`(request().withPath("/api"))
            .respond(
                response()
                    .withHeaders(
                        Header(CONTENT_TYPE.toString(), MediaType.PLAIN_TEXT_UTF_8.toString())
                    )
                    .withBody(TVals.vStr)
            )

        val result = accessor.readFrom("http://localhost:8888/api").use {
            IOUtils.toString(it, "utf-8")
        }
        assertEquals(TVals.vStr, result)

        server.verify(request().withPath("/api"), exactly(1))
    }

    @Test
    fun writeTo() {
        val input = TVals.vStr.toByteArray()
        val endpoint = request()
            .withMethod("POST")
            .withPath("/api")
            .withBody(input)
        val errorEndPoint = request()
            .withMethod("POST")
            .withPath("/error")
        val crashEndPoint = request()
            .withMethod("POST")
            .withPath("/crash")

        server.`when`(endpoint).respond(response())
        server
            .`when`(errorEndPoint)
            .respond(response().withStatusCode(HttpStatusCode.INTERNAL_SERVER_ERROR_500.code()))
        server
            .`when`(crashEndPoint)
            .error(HttpError().withDropConnection(true))

        accessor.writeTo("http://localhost:8888/api").use {
            it.write(input, 0, input.size)
        }

        try {
            accessor.writeTo("http://localhost:8888/error").use {
                it.write(input)
            }
            fail("Accessor should throw IOException due to error status code")
        } catch (e: IOException) {
            //  Should throws
        }

        try {
            accessor.writeTo("http://localhost:8888/crash").use {
                it.write(input, 0, input.size)
            }
            fail("Accessor should throw IOException for due to connection error")
        } catch (e: IOException) {
            //  Should throws
        }

        server.verify(endpoint, exactly(1))
        server.verify(errorEndPoint, exactly(1))
    }

    companion object {

        @JvmStatic
        @BeforeAll
        fun init() {
            server = ClientAndServer.startClientAndServer(8888)
        }

        @JvmStatic
        @AfterAll
        fun destroy() {
            server.stop()
        }

        private lateinit var server: ClientAndServer
    }
}