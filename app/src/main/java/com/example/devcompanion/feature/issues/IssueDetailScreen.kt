package com.example.devcompanion.feature.issues

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun IssueDetailsScreen(
    owner: String,
    repo: String,
    issueNumber: Int,
    viewModel: IssueDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(owner, repo, issueNumber) {
        viewModel.loadIssue(owner, repo, issueNumber)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )

            Button(
                onClick = {
                    viewModel.loadIssue(owner, repo, issueNumber)
                }
            ) {
                Text("Retry")
            }
        }

        state.issue?.let { issue ->
            Text(
                text = "#${issue.number} ${issue.title}",
                style = MaterialTheme.typography.headlineSmall
            )

            Text("Status: ${issue.state}")
            Text("Author: ${issue.author}")
            Text("Created: ${issue.createdAt}")

            issue.updatedAt?.let {
                Text("Updated: $it")
            }

            issue.closedAt?.let {
                Text("Closed: $it")
            }

            if (issue.labels.isNotEmpty()) {
                Text("Labels: ${issue.labels.joinToString()}")
            }

            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium
            )

            Text(issue.body?.takeIf { it.isNotBlank() }
                ?: "No description provided.")

            issue.htmlUrl?.let { url ->
                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(url)
                        )
                        context.startActivity(intent)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Open on GitHub")
                }
            }
        }
    }
}
