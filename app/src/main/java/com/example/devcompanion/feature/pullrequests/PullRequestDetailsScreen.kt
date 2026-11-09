package com.example.devcompanion.feature.pullrequests

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
fun PullRequestDetailsScreen(
    owner: String,
    repo: String,
    pullNumber: Int,
    viewModel: PullRequestDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(owner, repo, pullNumber) {
        viewModel.loadPullRequest(owner, repo, pullNumber)
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
                    viewModel.loadPullRequest(owner, repo, pullNumber)
                }
            ) {
                Text("Retry")
            }
        }

        state.pullRequest?.let { pull ->
            Text(
                text = "#${pull.number} ${pull.title}",
                style = MaterialTheme.typography.headlineSmall
            )

            val status = when {
                pull.isMerged -> "Merged"
                pull.state.equals("open", ignoreCase = true) -> "Open"
                else -> "Closed"
            }

            Text("Status: $status")
            Text("Author: ${pull.author}")
            Text("Created: ${pull.createdAt}")

            pull.updatedAt?.let {
                Text("Updated: $it")
            }

            pull.mergedAt?.let {
                Text("Merged: $it")
            }

            pull.closedAt?.let {
                Text("Closed: $it")
            }

            Text(
                text = "Branches",
                style = MaterialTheme.typography.titleMedium
            )

            Text("${pull.headBranch} → ${pull.baseBranch}")

            Text(
                text = "Description",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                pull.body?.takeIf { it.isNotBlank() }
                    ?: "No description provided."
            )

            pull.htmlUrl?.let { url ->
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
