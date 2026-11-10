package com.example.devcompanion.feature.repositories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun CommitsScreen(
    owner: String,
    repo: String,
    viewModel: RepositoryActivityViewModel = hiltViewModel()
) {
    val state by viewModel.commits.collectAsState()
    val branchesState by viewModel.branches.collectAsState()

    var expanded by remember { mutableStateOf(false) }

    LaunchedEffect(owner, repo) {
        viewModel.initializeBranches(owner, repo)
        viewModel.initializeCommits(owner, repo)
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row(
            modifier = Modifier.padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Branch:",
                style = MaterialTheme.typography.titleMedium
            )

            Box {
                Button(
                    onClick = { expanded = true },
                    enabled = branchesState.branches.isNotEmpty()
                ) {
                    Text(
                        branchesState.selectedBranch
                            ?: "Default branch"
                    )
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    branchesState.branches.forEach { branch ->
                        DropdownMenuItem(
                            text = { Text(branch.name) },
                            onClick = {
                                expanded = false
                                viewModel.selectBranch(
                                    owner = owner,
                                    repo = repo,
                                    branch = branch.name
                                )
                            }
                        )
                    }
                }
            }

            if (branchesState.isLoading) {
                CircularProgressIndicator()
            }
        }

        if (branchesState.error != null) {
            Text(
                text = branchesState.error.orEmpty(),
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }

        Box(
            modifier = Modifier.weight(1f)
        ) {
            RepositoryActivityScreen(
                state = state,
                onRetry = {
                    viewModel.loadCommits(owner, repo)
                },
                onLoadMore = {
                    viewModel.loadCommits(owner, repo)
                },
                itemKey = { it.sha }
            ) { commit ->
                Text(
                    commit.message
                        .lineSequence()
                        .firstOrNull()
                        .orEmpty()
                )
                Text("SHA: ${commit.sha.take(7)}")
                Text("Author: ${commit.author}")
                Text("Date: ${commit.date}")
            }
        }
    }
}
