
package com.example.devcompanion.feature.devtools

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Http
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Transform
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

private data class DevToolItem(
    val id: String,
    val title: String,
    val description: String,
    val icon: ImageVector
)

private val devTools = listOf(
    DevToolItem(
        id = "json",
        title = "JSON Formatter",
        description = "Validate and format JSON",
        icon = Icons.Default.Code
    ),
    DevToolItem(
        id = "http",
        title = "HTTP Client",
        description = "Send API requests",
        icon = Icons.Default.Http
    ),
    DevToolItem(
        id = "bcrypt",
        title = "bcrypt",
        description = "Generate and verify hashes",
        icon = Icons.Default.Lock
    ),
    DevToolItem(
        id = "uuid",
        title = "UUID",
        description = "Generate unique identifiers",
        icon = Icons.Default.Fingerprint
    ),
    DevToolItem(
        id = "regex",
        title = "Regex Tester",
        description = "Test regular expressions",
        icon = Icons.Default.Build
    ),
    DevToolItem(
        id = "timestamp",
        title = "Timestamp",
        description = "Convert timestamps",
        icon = Icons.Default.Schedule
    ),
    DevToolItem(
        id = "urlcodec",
        title = "URL Codec",
        description = "Encode and decode URLs",
        icon = Icons.Default.Link
    ),
    DevToolItem(
        id = "hash",
        title = "Hash Generator",
        description = "SHA-256 and SHA-512",
        icon = Icons.Default.Transform
    ),
    DevToolItem(
        id = "diff",
        title = "Text Diff",
        description = "Compare text",
        icon = Icons.Default.TextFields
    ),
    DevToolItem(
        id = "color",
        title = "Color Converter",
        description = "HEX, RGB and HSL",
        icon = Icons.Default.ColorLens
    )
)

@Composable
fun DevToolsScreen(
    onToolClick: (toolId: String) -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "DevTools",
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            text = "Programming utilities",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = devTools,
                key = { it.id }
            ) { tool ->
                DevToolCard(
                    tool = tool,
                    onClick = {
                        onToolClick(tool.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun DevToolCard(
    tool: DevToolItem,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                imageVector = tool.icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )

            Text(
                text = tool.title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = tool.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
