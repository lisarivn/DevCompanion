
package com.example.devcompanion.feature.devtools.timestamp

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.time.ZoneId

@Composable
fun TimestampConverterScreen() {
    val context = LocalContext.current

    var timestampToDate by rememberSaveable {
        mutableStateOf(true)
    }

    var useMilliseconds by rememberSaveable {
        mutableStateOf(false)
    }

    var selectedZone by rememberSaveable {
        mutableStateOf("UTC")
    }

    var input by rememberSaveable {
        mutableStateOf("")
    }

    var output by rememberSaveable {
        mutableStateOf("")
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    val deviceZone = ZoneId.systemDefault().id

    val availableZones = listOf(
        "UTC",
        "Europe/Kyiv",
        deviceZone
    ).distinct()

    val unit = if (useMilliseconds) {
        TimestampConverter.Unit.MILLISECONDS
    } else {
        TimestampConverter.Unit.SECONDS
    }

    fun resetResult() {
        output = ""
        errorMessage = ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Timestamp Converter",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Convert Unix timestamps and dates with time zones.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "Conversion direction",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = timestampToDate,
                onClick = {
                    timestampToDate = true
                    input = ""
                    resetResult()
                },
                label = { Text("Timestamp → Date") }
            )

            FilterChip(
                selected = !timestampToDate,
                onClick = {
                    timestampToDate = false
                    input = ""
                    resetResult()
                },
                label = { Text("Date → Timestamp") }
            )
        }

        Text(
            text = "Unix timestamp unit",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = !useMilliseconds,
                onClick = {
                    useMilliseconds = false
                    resetResult()
                },
                label = { Text("Seconds") }
            )

            FilterChip(
                selected = useMilliseconds,
                onClick = {
                    useMilliseconds = true
                    resetResult()
                },
                label = { Text("Milliseconds") }
            )
        }

        Text(
            text = "Time zone",
            style = MaterialTheme.typography.titleMedium
        )

        availableZones.forEach { zone ->
            FilterChip(
                selected = selectedZone == zone,
                onClick = {
                    selectedZone = zone
                    resetResult()
                },
                label = { Text(zone) }
            )
        }

        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it
                resetResult()
            },
            label = {
                Text(
                    if (timestampToDate) {
                        "Unix timestamp"
                    } else {
                        "Date and time"
                    }
                )
            },
            placeholder = {
                Text(
                    if (timestampToDate) {
                        "1700000000"
                    } else {
                        "2026-10-21 12:30:00"
                    }
                )
            },
            supportingText = {
                if (!timestampToDate) {
                    Text("Format: yyyy-MM-dd HH:mm:ss")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Button(
            onClick = {
                val zone = ZoneId.of(selectedZone)

                if (timestampToDate) {
                    val timestamp = input.trim().toLongOrNull()

                    if (timestamp == null) {
                        output = ""
                        errorMessage = "Enter a valid integer timestamp"
                    } else {
                        TimestampConverter.timestampToDate(
                            timestamp,
                            unit,
                            zone
                        ).onSuccess { result ->
                            output = result
                            errorMessage = ""
                        }.onFailure { error ->
                            output = ""
                            errorMessage = error.message
                                ?: "Conversion failed"
                        }
                    }
                } else {
                    TimestampConverter.dateToTimestamp(
                        input.trim(),
                        unit,
                        zone
                    ).onSuccess { result ->
                        output = result.toString()
                        errorMessage = ""
                    }.onFailure { error ->
                        output = ""
                        errorMessage = error.message
                            ?: "Conversion failed"
                    }
                }
            },
            enabled = input.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Convert")
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        OutlinedTextField(
            value = output,
            onValueChange = {},
            readOnly = true,
            label = { Text("Result") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedButton(
            onClick = {
                val clipboard = context.getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager

                clipboard.setPrimaryClip(
                    ClipData.newPlainText(
                        "Timestamp Result",
                        output
                    )
                )
            },
            enabled = output.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Copy result")
        }
    }
}
