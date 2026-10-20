package com.example.devcompanion.feature.devtools.uuid

import java.util.UUID
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class UuidGeneratorTest {

    @Test
    fun generate_returnsValidUuidV4() {
        val result = UuidGenerator.generate()

        val parsedUuid = UUID.fromString(result)

        assertEquals(4, parsedUuid.version())
        assertEquals(36, result.length)
        assertTrue(
            result.matches(
                Regex(
                    "^[0-9a-f]{8}-[0-9a-f]{4}-4[0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$"
                )
            )
        )
    }

    @Test
    fun generate_returnsDifferentValues() {
        val firstUuid = UuidGenerator.generate()
        val secondUuid = UuidGenerator.generate()

        assertNotEquals(firstUuid, secondUuid)
    }
}