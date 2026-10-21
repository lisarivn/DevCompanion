
package com.example.devcompanion.feature.devtools.regex

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.remember

@Composable
fun RegexCheckerScreen() {
    var pattern by rememberSaveable {
        mutableStateOf("")
    }

    var inputText by rememberSaveable {
        mutableStateOf("")
    }

    var result by remember {
        mutableStateOf<RegexChecker.CheckResult?>(null)
    }

    var errorMessage by rememberSaveable {
        mutableStateOf("")
    }

    var hasChecked by rememberSaveable {
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
            text = "Regex Checker",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Test regular expressions and inspect matches.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        OutlinedTextField(
            value = pattern,
            onValueChange = {
                pattern = it
                result = null
                errorMessage = ""
                hasChecked = false
            },
            label = { Text("Regular expression") },
            placeholder = { Text("\\d+") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = inputText,
            onValueChange = {
                inputText = it
                result = null
                errorMessage = ""
                hasChecked = false
            },
            label = { Text("Test text") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )

        Button(
            onClick = {
                RegexChecker.check(
                    pattern = pattern,
                    input = inputText
                ).onSuccess {
                    result = it
                    errorMessage = ""
                }.onFailure {
                    result = null
                    errorMessage = it.message ?: "Invalid regular expression"
                }

                hasChecked = true
            },
            enabled = pattern.isNotEmpty(),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Check regex")
        }

        if (errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (hasChecked && result != null) {
            val currentResult = result!!

            Text(
                text = "Matches found: ${currentResult.matchCount}",
                style = MaterialTheme.typography.titleMedium
            )

            if (currentResult.matches.isEmpty()) {
                Text(
                    text = "No matches found.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            currentResult.matches.forEachIndexed { index, match ->
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "Match ${index + 1}",
                            style = MaterialTheme.typography.titleSmall
                        )

                        Text("Value: ${match.value}")

                        Text(
                            "Position: ${match.start} – ${match.endExclusive} (end exclusive)"
                        )

                        if (match.groups.isNotEmpty()) {
                            Text(
                                text = "Capturing groups:",
                                style = MaterialTheme.typography.labelLarge
                            )

                            match.groups.forEachIndexed { groupIndex, group ->
                                Text(
                                    text = "Group ${groupIndex + 1}: ${group ?: "(not matched)"}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
