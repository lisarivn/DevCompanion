
package com.example.devcompanion.feature.devtools.timestamp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.ZoneId

class TimestampConverterTest {

    private val utc = ZoneId.of("UTC")
    private val kyiv = ZoneId.of("Europe/Kyiv")

    @Test
    fun timestampToDate_convertsSeconds() {
        val result = TimestampConverter.timestampToDate(
            0L,
            TimestampConverter.Unit.SECONDS,
            utc
        )

        assertEquals(
            "1970-01-01 00:00:00",
            result.getOrThrow()
        )
    }

    @Test
    fun timestampToDate_convertsMilliseconds() {
        val result = TimestampConverter.timestampToDate(
            1000L,
            TimestampConverter.Unit.MILLISECONDS,
            utc
        )

        assertEquals(
            "1970-01-01 00:00:01",
            result.getOrThrow()
        )
    }

    @Test
    fun timestampToDate_appliesTimeZone() {
        val result = TimestampConverter.timestampToDate(
            0L,
            TimestampConverter.Unit.SECONDS,
            kyiv
        )

        assertEquals(
            "1970-01-01 03:00:00",
            result.getOrThrow()
        )
    }

    @Test
    fun dateToTimestamp_convertsSeconds() {
        val result = TimestampConverter.dateToTimestamp(
            "1970-01-01 00:00:00",
            TimestampConverter.Unit.SECONDS,
            utc
        )

        assertEquals(0L, result.getOrThrow())
    }

    @Test
    fun dateToTimestamp_convertsMilliseconds() {
        val result = TimestampConverter.dateToTimestamp(
            "1970-01-01 00:00:01",
            TimestampConverter.Unit.MILLISECONDS,
            utc
        )

        assertEquals(1000L, result.getOrThrow())
    }

    @Test
    fun dateToTimestamp_rejectsInvalidDate() {
        val result = TimestampConverter.dateToTimestamp(
            "2026-02-30 12:00:00",
            TimestampConverter.Unit.SECONDS,
            utc
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun dateToTimestamp_rejectsNonexistentLocalTime() {
        val result = TimestampConverter.dateToTimestamp(
            "2026-03-29 03:30:00",
            TimestampConverter.Unit.SECONDS,
            kyiv
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun dateToTimestamp_rejectsAmbiguousLocalTime() {
        val result = TimestampConverter.dateToTimestamp(
            "2026-10-25 03:30:00",
            TimestampConverter.Unit.SECONDS,
            kyiv
        )

        assertTrue(result.isFailure)
    }

    @Test
    fun conversion_roundTrip() {
        val original = 1_700_000_000L

        val date = TimestampConverter.timestampToDate(
            original,
            TimestampConverter.Unit.SECONDS,
            utc
        ).getOrThrow()

        val restored = TimestampConverter.dateToTimestamp(
            date,
            TimestampConverter.Unit.SECONDS,
            utc
        ).getOrThrow()

        assertEquals(original, restored)
    }
}
