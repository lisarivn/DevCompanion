
package com.example.devcompanion.feature.devtools.json

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import com.google.gson.Strictness
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.io.StringReader

object JsonFormatter {

    private val prettyGson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    private val compactGson = GsonBuilder()
        .create()

    fun validate(input: String): Result<Unit> {
        return runCatching {
            parse(input)
            Unit
        }
    }

    fun prettyPrint(input: String): Result<String> {
        return runCatching {
            prettyGson.toJson(parse(input))
        }
    }

    fun minify(input: String): Result<String> {
        return runCatching {
            compactGson.toJson(parse(input))
        }
    }

    private fun parse(input: String): JsonElement {
        if (input.isBlank()) {
            throw IllegalArgumentException("JSON input is empty")
        }

        JsonReader(StringReader(input)).use { reader ->
            reader.strictness = Strictness.STRICT

            val element = JsonParser.parseReader(reader)

            if (reader.peek() != JsonToken.END_DOCUMENT) {
                throw IllegalArgumentException(
                    "Unexpected characters after JSON"
                )
            }

            if (!element.isJsonObject && !element.isJsonArray) {
                throw IllegalArgumentException(
                    "JSON must be an object or array"
                )
            }

            return element
        }
    }
}
