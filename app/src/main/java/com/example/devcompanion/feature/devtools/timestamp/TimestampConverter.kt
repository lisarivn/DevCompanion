
package com.example.devcompanion.feature.devtools.timestamp

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

object TimestampConverter {

    private val formatter = DateTimeFormatter
        .ofPattern("uuuu-MM-dd HH:mm:ss")
        .withResolverStyle(ResolverStyle.STRICT)

    enum class Unit {
        SECONDS,
        MILLISECONDS
    }

    fun timestampToDate(
        timestamp: Long,
        unit: Unit,
        zoneId: ZoneId
    ): Result<String> {
        return runCatching {
            val instant = when (unit) {
                Unit.SECONDS -> Instant.ofEpochSecond(timestamp)
                Unit.MILLISECONDS -> Instant.ofEpochMilli(timestamp)
            }

            formatter.format(
                instant.atZone(zoneId)
            )
        }
    }

    fun dateToTimestamp(
        dateTime: String,
        unit: Unit,
        zoneId: ZoneId
    ): Result<Long> {
        return runCatching {
            val localDateTime = LocalDateTime.parse(
                dateTime,
                formatter
            )

            val validOffsets = zoneId.rules
                .getValidOffsets(localDateTime)

            require(validOffsets.size == 1) {
                "Ambiguous or nonexistent local date/time"
            }

            val instant = localDateTime
                .atOffset(validOffsets.single())
                .toInstant()

            when (unit) {
                Unit.SECONDS -> instant.epochSecond
                Unit.MILLISECONDS -> instant.toEpochMilli()
            }
        }
    }
}
