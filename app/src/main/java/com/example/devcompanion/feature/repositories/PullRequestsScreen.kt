package com.example.devcompanion.feature.repositories

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun PullRequestsScreen(
    owner: String,
    repo: String,
    viewModel: RepositoryActivityViewModel = hiltViewModel()
) {
    val state by viewModel.pullRequests.collectAsState()

    LaunchedEffect(owner, repo) {
        viewModel.initializePullRequests(owner, repo)
    }

    RepositoryActivityScreen(
        state = state,
        onRetry = { viewModel.loadPullRequests(owner, repo) },
        onLoadMore = { viewModel.loadPullRequests(owner, repo) },
        itemKey = { it.id }
    ) { pull ->
        Text("#${pull.number} ${pull.title}")
        Text(
            if (pull.isMerged) "State: Merged"
            else "State: ${pull.state}"
        )
        Text("Author: ${pull.author}")
        Text("${pull.headBranch} → ${pull.baseBranch}")
        Text("Created: ${pull.createdAt}")
    }
}