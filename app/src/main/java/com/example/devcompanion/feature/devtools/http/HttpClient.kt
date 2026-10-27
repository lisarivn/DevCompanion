package com.example.devcompanion.feature.devtools.http

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class HttpClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .callTimeout(30, TimeUnit.SECONDS)
        .followRedirects(false)
        .build(),

    private val allowHttpForTesting: Boolean = false
) {

    data class HttpResponse(
        val statusCode: Int,
        val statusMessage: String,
        val body: String,
        val durationMs: Long,
        val headers: Map<String, String>
    )

    suspend fun execute(
        method: String,
        url: String,
        requestBody: String = ""
    ): Result<HttpResponse> = withContext(Dispatchers.IO) {
        runCatching {
            val normalizedMethod = method.uppercase()
            val normalizedUrl = url.trim()

            require(normalizedMethod in SUPPORTED_METHODS) {
                "Unsupported HTTP method"
            }

            val parsedUrl = normalizedUrl.toHttpUrlOrNull()
                ?: throw IllegalArgumentException("Invalid URL")

            require(
                parsedUrl.scheme == "https" ||
                        (allowHttpForTesting && parsedUrl.scheme == "http")
            ) {
                "Only HTTPS URLs are supported"
            }

            val body = when (normalizedMethod) {
                "POST", "PUT", "PATCH" -> {
                    requestBody.toRequestBody(
                        "application/json; charset=utf-8".toMediaType()
                    )
                }

                else -> null
            }

            val request = Request.Builder()
                .url(normalizedUrl)
                .method(normalizedMethod, body)
                .build()

            val startTime = System.nanoTime()

            client.newCall(request).execute().use { response ->
                val durationMs =
                    TimeUnit.NANOSECONDS.toMillis(
                        System.nanoTime() - startTime
                    )

                HttpResponse(
                    statusCode = response.code,
                    statusMessage = response.message,
                    body = response.body?.string().orEmpty(),
                    durationMs = durationMs,
                    headers = response.headers.names().associateWith { name ->
                        response.header(name).orEmpty()
                    }
                )
            }
        }
    }

    companion object {
        val SUPPORTED_METHODS = listOf(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE"
        )
    }
}