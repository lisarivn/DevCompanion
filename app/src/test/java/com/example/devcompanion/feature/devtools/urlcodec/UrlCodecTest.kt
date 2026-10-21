package com.example.devcompanion.feature.devtools.urlcodec

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UrlCodecTest {

    @Test
    fun encode_usesPercentEncodingForSpaces() {
        assertEquals(
            "Hello%20world%21",
            UrlCodec.encode("Hello world!")
        )
    }

    @Test
    fun encode_supportsUtf8Characters() {
        assertEquals(
            "%D0%9F%D1%80%D0%B8%D0%B2%D1%96%D1%82",
            UrlCodec.encode("Привіт")
        )
    }

    @Test
    fun encode_preservesUnreservedCharacters() {
        assertEquals(
            "abc-XYZ_123.~",
            UrlCodec.encode("abc-XYZ_123.~")
        )
    }

    @Test
    fun decode_restoresOriginalText() {
        assertEquals(
            "Hello world!",
            UrlCodec.decode("Hello%20world%21").getOrThrow()
        )
    }

    @Test
    fun decode_rejectsInvalidPercentEncoding() {
        assertTrue(UrlCodec.decode("Hello%ZZ").isFailure)
        assertTrue(UrlCodec.decode("Hello%A").isFailure)
    }

    @Test
    fun decode_rejectsInvalidUtf8() {
        assertTrue(UrlCodec.decode("%FF").isFailure)
    }

    @Test
    fun encodeAndDecode_preserveOriginalText() {
        val original = "DevCompanion: Привіт & Kotlin!"

        val encoded = UrlCodec.encode(original)
        val decoded = UrlCodec.decode(encoded).getOrThrow()

        assertEquals(original, decoded)
    }

    @Test
    fun decode_doesNotTreatPlusAsSpace() {
        assertEquals(
            "a+b",
            UrlCodec.decode("a+b").getOrThrow()
        )
    }
}
