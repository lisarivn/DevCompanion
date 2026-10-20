package com.example.devcompanion.data.remote

import com.example.devcompanion.domain.model.PageResult
import retrofit2.HttpException
import retrofit2.Response

fun <T, R> Response<T>.toPageResult(
    extractItems: (T) -> List<R>
): PageResult<R> {

    if (!isSuccessful) {
        throw HttpException(this)
    }

    val responseBody = body()
        ?: throw IllegalStateException("Empty API response.")

    val linkHeader = headers()["Link"]

    val nextPage = linkHeader
        ?.split(",")
        ?.firstOrNull { it.contains("rel=\"next\"") }
        ?.let { link ->
            Regex("""[?&]page=(\d+)""")
                .find(link)
                ?.groupValues
                ?.get(1)
                ?.toIntOrNull()
        }

    return PageResult(
        items = extractItems(responseBody),
        nextPage = nextPage
    )
}