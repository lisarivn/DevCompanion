package com.example.devcompanion.feature.repositories

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CommitsScreen(
    owner: String,
    repo: String,
    viewModel: RepositoryActivityViewModel = hiltViewModel()
) {
    val state by viewModel.commits.collectAsState()

    LaunchedEffect(owner, repo) {
        viewModel.initializeCommits(owner, repo)
    }

    RepositoryActivityScreen(
        state = state,
        onRetry = { viewModel.loadCommits(owner, repo) },
        onLoadMore = { viewModel.loadCommits(owner, repo) },
        itemKey = { it.sha }
    ) { commit ->
        Text(commit.message.lineSequence().firstOrNull().orEmpty())
        Text("SHA: ${commit.sha.take(7)}")
        Text("Author: ${commit.author}")
        Text("Date: ${commit.date}")
    }
}