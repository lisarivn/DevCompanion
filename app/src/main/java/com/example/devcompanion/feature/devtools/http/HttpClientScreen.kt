package com.example.devcompanion.feature.devtools.http

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun HttpClientScreen() {

    val httpClient = remember { HttpClient() }
    val scope = rememberCoroutineScope()

    var selectedMethod by rememberSaveable {
        mutableStateOf("GET")
    }

    var url by rememberSaveable {
        mutableStateOf("")
    }

    var requestBody by rememberSaveable {
        mutableStateOf("")
    }

    var isLoading by remember {
        mutableStateOf(false)
    }

    var response by remember {
        mutableStateOf<HttpClient.HttpResponse?>(null)
    }

    var errorMessage by remember {
        mutableStateOf<String?>(null)
    }

    fun sendRequest() {
        if (isLoading) return

        scope.launch {
            isLoading = true
            response = null
            errorMessage = null

            try {
                httpClient.execute(
                    method = selectedMethod,
                    url = url,
                    requestBody = requestBody
                )
                    .onSuccess {
                        response = it
                    }
                    .onFailure {
                        errorMessage =
                            it.message ?: "Request failed"
                    }
            } finally {
                isLoading = false
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
            text = "HTTP Client",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = "Send HTTP requests and inspect server responses.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = "HTTP Method",
            style = MaterialTheme.typography.titleMedium
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("GET", "POST", "PUT").forEach { method ->
                FilterChip(
                    selected = selectedMethod == method,
                    onClick = {
                        selectedMethod = method
                    },
                    label = {
                        Text(method)
                    }
                )
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf("PATCH", "DELETE").forEach { method ->
                FilterChip(
                    selected = selectedMethod == method,
                    onClick = {
                        selectedMethod = method
                    },
                    label = {
                        Text(method)
                    }
                )
            }
        }

        OutlinedTextField(
            value = url,
            onValueChange = { url = it },
            label = {
                Text("Request URL")
            },
            placeholder = {
                Text("https://api.github.com")
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        if (selectedMethod in listOf("POST", "PUT", "PATCH")) {
            OutlinedTextField(
                value = requestBody,
                onValueChange = {
                    requestBody = it
                },
                label = {
                    Text("JSON Request Body")
                },
                placeholder = {
                    Text("""{"name":"Example"}""")
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 120.dp),
                minLines = 4
            )
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Button(
                onClick = {
                    sendRequest()
                },
                enabled = !isLoading && url.isNotBlank()
            ) {
                Text("Send Request")
            }

            OutlinedButton(
                onClick = {
                    selectedMethod = "GET"
                    url = "https://api.github.com"
                    requestBody = ""
                    response = null
                    errorMessage = null
                },
                enabled = !isLoading
            ) {
                Text("Load Example")
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
            Text("Sending request...")
        }

        errorMessage?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        response?.let { result ->

            HorizontalDivider()

            Text(
                text = "Response",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            val statusColor = when (result.statusCode) {
                in 200..299 -> MaterialTheme.colorScheme.primary
                in 400..599 -> MaterialTheme.colorScheme.error
                else -> MaterialTheme.colorScheme.onSurface
            }

            Text(
                text = "Status: ${result.statusCode} ${result.statusMessage}",
                color = statusColor,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Duration: ${result.durationMs} ms",
                style = MaterialTheme.typography.bodyMedium
            )

            Text(
                text = "Response Body",
                style = MaterialTheme.typography.titleMedium
            )

            OutlinedTextField(
                value = result.body,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 180.dp),
                minLines = 6
            )

            Text(
                text = "Response Headers",
                style = MaterialTheme.typography.titleMedium
            )

            val headersText = result.headers.entries.joinToString("\n") {
                "${it.key}: ${it.value}"
            }

            OutlinedTextField(
                value = headersText,
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 100.dp)
            )
        }
    }
}