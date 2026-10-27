package com.example.devcompanion.data.remote

import com.example.devcompanion.domain.model.PageResult
import retrofit2.HttpException
import retrofit2.Response

private val PAGE_REGEX = Regex("""[?&]page=(\d+)""")
private val NEXT_REL_REGEX = Regex("""\brel\s*=\s*"?next"?\b""", RegexOption.IGNORE_CASE)

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
        ?.firstOrNull { link ->
            NEXT_REL_REGEX.containsMatchIn(link)
        }
        ?.let { link ->
            PAGE_REGEX.find(link)
                ?.groupValues
                ?.getOrNull(1)
                ?.toIntOrNull()
        }

    return PageResult(
        items = extractItems(responseBody),
        nextPage = nextPage
    )
}