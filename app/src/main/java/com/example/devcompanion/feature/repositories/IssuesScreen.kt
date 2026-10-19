package com.example.devcompanion.feature.repositories

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun IssuesScreen(
    owner: String,
    repo: String,
    viewModel: RepositoryActivityViewModel = hiltViewModel()
) {
    val state by viewModel.issues.collectAsState()

    LaunchedEffect(owner, repo) {
        viewModel.loadIssues(owner, repo)
    }

    RepositoryActivityScreen(
        state = state,
        onRetry = { viewModel.loadIssues(owner, repo) },
        itemKey = { it.id }
    ) { issue ->
        Text("#${issue.number} ${issue.title}")
        Text("State: ${issue.state}")
        Text("Author: ${issue.author}")
        Text("Created: ${issue.createdAt}")
        if (issue.labels.isNotEmpty()) {
            Text("Labels: ${issue.labels.joinToString()}")
        }
    }
}