
package com.example.devcompanion.feature.devtools.regex

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class RegexCheckerTest {

    @Test
    fun findsSingleMatch() {
        val result = RegexChecker.check(
            pattern = "\\d+",
            input = "Order 123"
        ).getOrThrow()

        assertEquals(1, result.matchCount)
        assertEquals("123", result.matches[0].value)
    }

    @Test
    fun findsMultipleMatches() {
        val result = RegexChecker.check(
            pattern = "\\d+",
            input = "12 apples and 34 oranges"
        ).getOrThrow()

        assertEquals(2, result.matchCount)
        assertEquals("12", result.matches[0].value)
        assertEquals("34", result.matches[1].value)
    }

    @Test
    fun returnsCorrectPositions() {
        val result = RegexChecker.check(
            pattern = "cat",
            input = "My cat sleeps"
        ).getOrThrow()

        val match = result.matches.first()

        assertEquals(3, match.start)
        assertEquals(6, match.endExclusive)
    }

    @Test
    fun returnsNoMatchesWhenPatternDoesNotMatch() {
        val result = RegexChecker.check(
            pattern = "\\d+",
            input = "Hello world"
        ).getOrThrow()

        assertEquals(0, result.matchCount)
        assertTrue(result.matches.isEmpty())
    }

    @Test
    fun returnsCapturingGroups() {
        val result = RegexChecker.check(
            pattern = "(\\w+)@(\\w+\\.\\w+)",
            input = "test@example.com"
        ).getOrThrow()

        val match = result.matches.first()

        assertEquals("test@example.com", match.value)
        assertEquals("test", match.groups[0])
        assertEquals("example.com", match.groups[1])
    }

    @Test
    fun returnsNullForUnmatchedOptionalGroup() {
        val result = RegexChecker.check(
            pattern = "(a)?b",
            input = "b"
        ).getOrThrow()

        assertEquals(1, result.matchCount)
        assertNull(result.matches.first().groups[0])
    }

    @Test
    fun returnsFailureForInvalidPattern() {
        val result = RegexChecker.check(
            pattern = "[abc",
            input = "abc"
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun supportsCaseInsensitiveMatching() {
        val result = RegexChecker.check(
            pattern = "(?i)hello",
            input = "HELLO hello"
        ).getOrThrow()

        assertEquals(2, result.matchCount)
    }

    @Test
    fun supportsUnicodeText() {
        val result = RegexChecker.check(
            pattern = "Привіт",
            input = "Привіт, Kotlin!"
        ).getOrThrow()

        assertEquals(1, result.matchCount)
        assertEquals("Привіт", result.matches.first().value)
    }

    @Test
    fun handlesEmptyInput() {
        val result = RegexChecker.check(
            pattern = "\\d+",
            input = ""
        ).getOrThrow()

        assertEquals(0, result.matchCount)
    }

    @Test
    fun handlesZeroLengthMatches() {
        val result = RegexChecker.check(
            pattern = "^",
            input = "abc"
        ).getOrThrow()

        assertEquals(1, result.matchCount)
        assertEquals("", result.matches.first().value)
        assertEquals(0, result.matches.first().start)
        assertEquals(0, result.matches.first().endExclusive)
    }
}
