package com.example.devcompanion.feature.repositories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.layout.Row


@Composable
fun IssuesScreen(
    owner: String,
    repo: String,
    onIssueClick: (Int) -> Unit,
    viewModel: RepositoryActivityViewModel = hiltViewModel()
) {

    val state by viewModel.issues.collectAsState()

    LaunchedEffect(state.repositoryKey, state.hasLoaded, state.error) {
        android.util.Log.d(
            "IssuesDebug",
            "key=${state.repositoryKey}, " +
                    "loaded=${state.hasLoaded}, " +
                    "loading=${state.isLoading}, " +
                    "items=${state.items.size}, " +
                    "nextPage=${state.nextPage}, " +
                    "error=${state.error}"
        )
    }

    LaunchedEffect(owner, repo) {
        viewModel.initializeIssues(owner, repo)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            listOf(
                "Open" to "open",
                "Closed" to "closed",
                "All" to "all"
            ).forEach { (label, filter) ->
                FilterChip(
                    selected = state.issueFilter == filter,
                    onClick = {
                        viewModel.updateIssuesFilter(
                            owner = owner,
                            repo = repo,
                            filter = filter
                        )
                    },
                    label = { Text(label) }
                )
            }
        }

        androidx.compose.foundation.layout.Box(
            modifier = Modifier.weight(1f)
        ) {
            RepositoryActivityScreen(
                state = state,
                onRetry = { viewModel.loadIssues(owner, repo) },
                onLoadMore = { viewModel.loadIssues(owner, repo) },
                itemKey = { it.id },
                onItemClick = { issue ->
                    onIssueClick(issue.number)
                }
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
    }
}
