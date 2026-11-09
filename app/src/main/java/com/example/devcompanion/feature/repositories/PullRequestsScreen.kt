package com.example.devcompanion.feature.repositories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
                    selected = state.pullRequestFilter == filter,
                    onClick = {
                        viewModel.updatePullRequestsFilter(
                            owner = owner,
                            repo = repo,
                            filter = filter
                        )
                    },
                    label = { Text(label) }
                )
            }
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            RepositoryActivityScreen(
                state = state,
                onRetry = {
                    viewModel.loadPullRequests(owner, repo)
                },
                onLoadMore = {
                    viewModel.loadPullRequests(owner, repo)
                },
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
    }
}
