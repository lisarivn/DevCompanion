
package com.example.devcompanion.feature.devtools.urlcodec

import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.nio.charset.CharacterCodingException
import java.nio.charset.CodingErrorAction
import java.nio.charset.StandardCharsets

object UrlCodec {

    private val HEX = "0123456789ABCDEF"

    fun encode(input: String): String {
        val bytes = input.toByteArray(StandardCharsets.UTF_8)
        val result = StringBuilder()

        for (byte in bytes) {
            val value = byte.toInt() and 0xFF

            if (isUnreserved(value)) {
                result.append(value.toChar())
            } else {
                result.append('%')
                result.append(HEX[value shr 4])
                result.append(HEX[value and 0x0F])
            }
        }

        return result.toString()
    }

    fun decode(input: String): Result<String> {
        return runCatching {
            val bytes = ByteArrayOutputStream()
            var index = 0

            while (index < input.length) {
                if (input[index] == '%') {
                    if (index + 2 >= input.length) {
                        throw IllegalArgumentException(
                            "Incomplete percent-encoded sequence"
                        )
                    }

                    val high = input[index + 1].digitToIntOrNull(16)
                    val low = input[index + 2].digitToIntOrNull(16)

                    if (high == null || low == null) {
                        throw IllegalArgumentException(
                            "Invalid percent-encoded sequence"
                        )
                    }

                    bytes.write((high shl 4) or low)
                    index += 3
                } else {
                    val codePoint = Character.codePointAt(input, index)
                    val character = String(Character.toChars(codePoint))

                    bytes.write(
                        character.toByteArray(StandardCharsets.UTF_8)
                    )

                    index += Character.charCount(codePoint)
                }
            }

            val decoder = StandardCharsets.UTF_8.newDecoder()
                .onMalformedInput(CodingErrorAction.REPORT)
                .onUnmappableCharacter(CodingErrorAction.REPORT)

            try {
                decoder.decode(
                    ByteBuffer.wrap(bytes.toByteArray())
                ).toString()
            } catch (error: CharacterCodingException) {
                throw IllegalArgumentException(
                    "Invalid UTF-8 sequence",
                    error
                )
            }
        }
    }

    private fun isUnreserved(value: Int): Boolean {
        return value in 'A'.code..'Z'.code ||
                value in 'a'.code..'z'.code ||
                value in '0'.code..'9'.code ||
                value == '-'.code ||
                value == '_'.code ||
                value == '.'.code ||
                value == '~'.code
    }
}
