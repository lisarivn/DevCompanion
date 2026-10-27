package com.example.devcompanion.feature.devtools.http

import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.util.concurrent.TimeUnit

class HttpClientTest {

    private lateinit var server: MockWebServer
    private lateinit var httpClient: HttpClient

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()

        val okHttpClient = OkHttpClient.Builder()
            .callTimeout(5, TimeUnit.SECONDS)
            .followRedirects(false)
            .build()

        httpClient = HttpClient(
            client = okHttpClient,
            allowHttpForTesting = true
        )
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun url(path: String = "/"): String {
        return server.url(path).toString()
    }

    @Test
    fun getRequestReturnsResponse() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"message":"Hello"}""")
        )

        val result = httpClient.execute(
            method = "GET",
            url = url("/hello")
        )

        assertTrue(
            "Request failed: ${result.exceptionOrNull()?.message}",
            result.isSuccess
        )

        val response = result.getOrThrow()

        assertEquals(200, response.statusCode)
        assertEquals("""{"message":"Hello"}""", response.body)

        val request = server.takeRequest()

        assertEquals("GET", request.method)
        assertEquals("/hello", request.path)
    }

    @Test
    fun postRequestSendsJsonBody() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(201)
                .setBody("""{"created":true}""")
        )

        val json = """{"name":"DevCompanion"}"""

        val result = httpClient.execute(
            method = "POST",
            url = url("/posts"),
            requestBody = json
        )

        assertTrue(result.isSuccess)
        assertEquals(201, result.getOrThrow().statusCode)

        val request = server.takeRequest()

        assertEquals("POST", request.method)
        assertEquals(json, request.body.readUtf8())

        assertTrue(
            request.getHeader("Content-Type")
                ?.startsWith("application/json") == true
        )
    }

    @Test
    fun putRequestSendsJsonBody() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody("""{"updated":true}""")
        )

        val json = """{"name":"Updated"}"""

        val result = httpClient.execute(
            method = "PUT",
            url = url("/posts/1"),
            requestBody = json
        )

        assertTrue(result.isSuccess)

        val request = server.takeRequest()

        assertEquals("PUT", request.method)
        assertEquals(json, request.body.readUtf8())
    }

    @Test
    fun patchRequestSendsJsonBody() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
        )

        val result = httpClient.execute(
            method = "PATCH",
            url = url("/posts/1"),
            requestBody = """{"name":"New"}"""
        )

        assertTrue(result.isSuccess)
        assertEquals("PATCH", server.takeRequest().method)
    }

    @Test
    fun deleteRequestWorks() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(204)
        )

        val result = httpClient.execute(
            method = "DELETE",
            url = url("/posts/1")
        )

        assertTrue(result.isSuccess)
        assertEquals(204, result.getOrThrow().statusCode)
        assertEquals("DELETE", server.takeRequest().method)
    }

    @Test
    fun notFoundResponseIsNotExecutionFailure() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setBody("Not Found")
        )

        val result = httpClient.execute(
            method = "GET",
            url = url("/missing")
        )

        assertTrue(result.isSuccess)
        assertEquals(404, result.getOrThrow().statusCode)
    }

    @Test
    fun serverErrorResponseIsNotExecutionFailure() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error")
        )

        val result = httpClient.execute(
            method = "GET",
            url = url("/error")
        )

        assertTrue(result.isSuccess)
        assertEquals(500, result.getOrThrow().statusCode)
    }

    @Test
    fun unsupportedMethodReturnsFailure() = runBlocking {
        val result = httpClient.execute(
            method = "OPTIONS",
            url = url()
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun invalidUrlReturnsFailure() = runBlocking {
        val result = httpClient.execute(
            method = "GET",
            url = "not-a-url"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun responseContainsHeaders() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .addHeader("X-Test", "DevCompanion")
        )

        val result = httpClient.execute(
            method = "GET",
            url = url()
        )

        assertTrue(result.isSuccess)

        assertEquals(
            "DevCompanion",
            result.getOrThrow().headers["X-Test"]
        )
    }

    @Test
    fun responseDurationIsNotNegative() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
        )

        val result = httpClient.execute(
            method = "GET",
            url = url()
        )

        assertTrue(result.isSuccess)
        assertTrue(result.getOrThrow().durationMs >= 0)
    }

    @Test
    fun responseWithRedirectIsReturnedWithoutFollowing() = runBlocking {
        server.enqueue(
            MockResponse()
                .setResponseCode(302)
                .addHeader("Location", "/redirected")
        )

        val result = httpClient.execute(
            method = "GET",
            url = url()
        )

        assertTrue(result.isSuccess)
        assertEquals(302, result.getOrThrow().statusCode)
    }
}