package com.example.devcompanion.feature.devtools.urlcodec

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

@Composable
fun UrlCodecScreen() {
    val context = LocalContext.current

    var input by rememberSaveable {
        mutableStateOf("")
    }

    var output by rememberSaveable {
        mutableStateOf("")
    }

    var message by rememberSaveable {
        mutableStateOf("")
    }

    var isError by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "URL Encoder / Decoder",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Encode and decode text using UTF-8",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = input,
            onValueChange = {
                input = it
                output = ""
                message = ""
            },
            label = { Text("Input text") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    output = UrlCodec.encode(input)
                    isError = false
                    message = "Text encoded successfully"
                },
                enabled = input.isNotEmpty()
            ) {
                Text("Encode")
            }

            OutlinedButton(
                onClick = {
                    UrlCodec.decode(input)
                        .onSuccess { result ->
                            output = result
                            isError = false
                            message = "Text decoded successfully"
                        }
                        .onFailure { error ->
                            output = ""
                            isError = true
                            message = error.message
                                ?: "Invalid URL encoding"
                        }
                },
                enabled = input.isNotEmpty()
            ) {
                Text("Decode")
            }
        }

        OutlinedTextField(
            value = output,
            onValueChange = {},
            label = { Text("Result") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4,
            readOnly = true
        )

        OutlinedButton(
            onClick = {
                val clipboard = context.getSystemService(
                    Context.CLIPBOARD_SERVICE
                ) as ClipboardManager

                clipboard.setPrimaryClip(
                    ClipData.newPlainText(
                        "URL Codec Result",
                        output
                    )
                )

                isError = false
                message = "Result copied to clipboard"
            },
            enabled = output.isNotEmpty()
        ) {
            Text("Copy")
        }

        if (message.isNotEmpty()) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                }
            )
        }
    }
}
