package com.example.devcompanion.feature.commits

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devcompanion.domain.model.GitHubCommitFile

@Composable
fun CommitDetailsScreen(
    owner: String,
    repo: String,
    sha: String,
    viewModel: CommitDetailsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(owner, repo, sha) {
        viewModel.loadCommitDetails(owner, repo, sha)
    }

    when {
        state.isLoading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }

        state.error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = state.error.orEmpty(),
                    color = MaterialTheme.colorScheme.error
                )

                Button(
                    onClick = {
                        viewModel.loadCommitDetails(
                            owner = owner,
                            repo = repo,
                            sha = sha,
                            forceRefresh = true
                        )
                    },
                    modifier = Modifier.padding(top = 16.dp)
                ) {
                    Text("Retry")
                }
            }
        }

        state.commit != null -> {
            val commit = state.commit!!

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    Text(
                        text = "Commit Details",
                        style = MaterialTheme.typography.headlineSmall
                    )
                }

                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = commit.message,
                                style = MaterialTheme.typography.titleMedium
                            )

                            HorizontalDivider()

                            Text("SHA: ${commit.sha}")
                            Text("Author: ${commit.author}")
                            Text("Date: ${commit.date}")
                        }
                    }
                }

                item {
                    Text(
                        text = "Changes",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                item {
                    val stats = commit.stats

                    if (stats != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            StatsCard(
                                label = "Files",
                                value = commit.files.size.toString(),
                                modifier = Modifier.weight(1f)
                            )

                            StatsCard(
                                label = "Added",
                                value = "+${stats.additions}",
                                modifier = Modifier.weight(1f)
                            )

                            StatsCard(
                                label = "Deleted",
                                value = "-${stats.deletions}",
                                modifier = Modifier.weight(1f)
                            )
                        }
                    } else {
                        Text("Statistics are unavailable.")
                    }
                }

                item {
                    Text(
                        text = "Changed files",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                if (commit.files.isEmpty()) {
                    item {
                        Text("No file information available.")
                    }
                } else {
                    items(
                        items = commit.files,
                        key = { it.filename }
                    ) { file ->
                        CommitFileCard(file)
                    }
                }

                item {
                    OutlinedButton(
                        onClick = {
                            val url = commit.htmlUrl
                                ?: "https://github.com/$owner/$repo/commit/${commit.sha}"

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

        else -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("Loading commit details...")
            }
        }
    }
}

@Composable
private fun StatsCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun CommitFileCard(
    file: GitHubCommitFile
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = file.filename,
                style = MaterialTheme.typography.titleSmall
            )

            Text("Status: ${file.status}")

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "+${file.additions}",
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "-${file.deletions}",
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
