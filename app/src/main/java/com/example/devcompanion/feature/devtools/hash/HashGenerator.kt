
package com.example.devcompanion.feature.devtools.hash

import java.nio.charset.StandardCharsets
import java.security.MessageDigest

object HashGenerator {

    enum class Algorithm(val algorithmName: String) {
        SHA256("SHA-256"),
        SHA512("SHA-512")
    }

    fun generate(
        input: String,
        algorithm: Algorithm
    ): String {
        val digest = MessageDigest.getInstance(
            algorithm.algorithmName
        )

        val hashBytes = digest.digest(
            input.toByteArray(StandardCharsets.UTF_8)
        )

        return hashBytes.joinToString("") { byte ->
            "%02x".format(byte.toInt() and 0xFF)
        }
    }
}
