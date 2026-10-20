
package com.example.devcompanion.feature.devtools.json

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.io.IOException

private const val MAX_IMPORT_BYTES = 1024 * 1024

@Composable
fun JsonFormatterScreen() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var jsonInput by rememberSaveable {
        mutableStateOf("")
    }

    var message by rememberSaveable {
        mutableStateOf("")
    }

    var isError by rememberSaveable {
        mutableStateOf(false)
    }

    var isProcessingFile by rememberSaveable {
        mutableStateOf(false)
    }

    fun showSuccess(text: String) {
        isError = false
        message = text
    }

    fun showError(error: Throwable) {
        isError = true
        message = error.message ?: "Operation failed"
    }

    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri: Uri? ->
        if (uri != null && !isProcessingFile) {
            coroutineScope.launch {
                isProcessingFile = true

                try {
                    val content = withContext(Dispatchers.IO) {
                        readTextFromUri(context, uri)
                    }

                    jsonInput = content
                    showSuccess("File imported successfully")
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    showError(error)
                } finally {
                    isProcessingFile = false
                }
            }
        }
    }

    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument(
            "application/json"
        )
    ) { uri: Uri? ->
        if (uri != null && !isProcessingFile) {
            val contentToExport = jsonInput

            coroutineScope.launch {
                isProcessingFile = true

                try {
                    withContext(Dispatchers.IO) {
                        writeTextToUri(
                            context = context,
                            uri = uri,
                            content = contentToExport
                        )
                    }

                    showSuccess("File exported successfully")
                } catch (error: CancellationException) {
                    throw error
                } catch (error: Exception) {
                    showError(error)
                } finally {
                    isProcessingFile = false
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "JSON Formatter",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Validate, format and minify JSON",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = jsonInput,
            onValueChange = {
                jsonInput = it
                message = ""
            },
            label = { Text("JSON input") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 10,
            enabled = !isProcessingFile
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = {
                    JsonFormatter.validate(jsonInput)
                        .onSuccess {
                            showSuccess("Valid JSON")
                        }
                        .onFailure(::showError)
                },
                enabled = !isProcessingFile
            ) {
                Text("Validate")
            }

            OutlinedButton(
                onClick = {
                    JsonFormatter.prettyPrint(jsonInput)
                        .onSuccess { result ->
                            jsonInput = result
                            showSuccess("JSON formatted successfully")
                        }
                        .onFailure(::showError)
                },
                enabled = !isProcessingFile
            ) {
                Text("Pretty")
            }

            OutlinedButton(
                onClick = {
                    JsonFormatter.minify(jsonInput)
                        .onSuccess { result ->
                            jsonInput = result
                            showSuccess("JSON minified successfully")
                        }
                        .onFailure(::showError)
                },
                enabled = !isProcessingFile
            ) {
                Text("Minify")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = {
                    val clipboard = context.getSystemService(
                        Context.CLIPBOARD_SERVICE
                    ) as ClipboardManager

                    clipboard.setPrimaryClip(
                        ClipData.newPlainText("JSON", jsonInput)
                    )

                    showSuccess("JSON copied to clipboard")
                },
                enabled = jsonInput.isNotEmpty() && !isProcessingFile
            ) {
                Text("Copy")
            }

            OutlinedButton(
                onClick = {
                    importLauncher.launch(
                        arrayOf(
                            "application/json",
                            "text/plain",
                            "application/octet-stream"
                        )
                    )
                },
                enabled = !isProcessingFile
            ) {
                Text("Import")
            }

            OutlinedButton(
                onClick = {
                    exportLauncher.launch("devcompanion.json")
                },
                enabled = jsonInput.isNotEmpty() && !isProcessingFile
            ) {
                Text("Export")
            }
        }

        if (isProcessingFile) {
            CircularProgressIndicator()
            Text(
                text = "Processing file...",
                style = MaterialTheme.typography.bodySmall
            )
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

private fun readTextFromUri(
    context: Context,
    uri: Uri
): String {
    return context.contentResolver.openInputStream(uri)?.use { input ->
        val output = ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        var totalBytes = 0

        while (true) {
            val count = input.read(buffer)
            if (count == -1) break

            totalBytes += count

            if (totalBytes > MAX_IMPORT_BYTES) {
                throw IOException(
                    "File is too large. Maximum size is 1 MB."
                )
            }

            output.write(buffer, 0, count)
        }

        output.toString(Charsets.UTF_8.name())
    } ?: throw IOException("Unable to open selected file")
}

private fun writeTextToUri(
    context: Context,
    uri: Uri,
    content: String
) {
    val output = context.contentResolver.openOutputStream(uri, "wt")
        ?: throw IOException("Unable to create file")

    output.use {
        it.write(content.toByteArray(Charsets.UTF_8))
        it.flush()
    }
}
