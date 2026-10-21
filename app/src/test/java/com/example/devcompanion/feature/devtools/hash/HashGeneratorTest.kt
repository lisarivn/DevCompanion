
package com.example.devcompanion.feature.devtools.hash

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Test

class HashGeneratorTest {

    @Test
    fun sha256_generatesCorrectHash() {
        val result = HashGenerator.generate(
            "abc",
            HashGenerator.Algorithm.SHA256
        )

        assertEquals(
            "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
            result
        )
    }

    @Test
    fun sha512_generatesCorrectHash() {
        val result = HashGenerator.generate(
            "abc",
            HashGenerator.Algorithm.SHA512
        )

        assertEquals(
            "ddaf35a193617abacc417349ae20413112e6fa4e89a97ea20a9eeee64b55d39a" +
                    "2192992a274fc1a836ba3c23a3feebbd454d4423643ce80e2a9ac94fa54ca49f",
            result
        )
    }

    @Test
    fun sha256_hasCorrectLength() {
        val result = HashGenerator.generate(
            "DevCompanion",
            HashGenerator.Algorithm.SHA256
        )

        assertEquals(64, result.length)
    }

    @Test
    fun sha512_hasCorrectLength() {
        val result = HashGenerator.generate(
            "DevCompanion",
            HashGenerator.Algorithm.SHA512
        )

        assertEquals(128, result.length)
    }

    @Test
    fun sameInput_producesSameHash() {
        val first = HashGenerator.generate(
            "Kotlin",
            HashGenerator.Algorithm.SHA256
        )

        val second = HashGenerator.generate(
            "Kotlin",
            HashGenerator.Algorithm.SHA256
        )

        assertEquals(first, second)
    }

    @Test
    fun differentInput_producesDifferentHash() {
        val first = HashGenerator.generate(
            "Hello",
            HashGenerator.Algorithm.SHA256
        )

        val second = HashGenerator.generate(
            "hello",
            HashGenerator.Algorithm.SHA256
        )

        assertNotEquals(first, second)
    }

    @Test
    fun utf8Text_isSupported() {
        val result = HashGenerator.generate(
            "Привіт",
            HashGenerator.Algorithm.SHA256
        )

        assertEquals(64, result.length)
    }

    @Test
    fun emptyInput_isSupported() {
        val result = HashGenerator.generate(
            "",
            HashGenerator.Algorithm.SHA256
        )

        assertEquals(
            "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
            result
        )
    }
}
