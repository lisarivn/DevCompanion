package com.example.devcompanion.feature.repositories

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun RepositoryDetailsScreen(
    onIssuesClick: () -> Unit,
    onPullRequestsClick: () -> Unit,
    onCommitsClick: () -> Unit,
    viewModel: RepositoryDetailsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    when {
        uiState.isLoading -> {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = uiState.error ?: "",
                    color = MaterialTheme.colorScheme.error
                )

                Button(onClick = viewModel::loadRepository) {
                    Text("Retry")
                }
            }
        }

        uiState.repository != null -> {
            val repository = uiState.repository!!

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = repository.fullName,
                    style = MaterialTheme.typography.headlineMedium
                )

                Text("Owner: ${repository.ownerLogin}")

                Text(
                    text = repository.description ?: "No description available.",
                    style = MaterialTheme.typography.bodyLarge
                )

                HorizontalDivider()

                Text("Language: ${repository.language ?: "Unknown"}")
                Text("Stars: ${repository.starsCount}")
                Text("Forks: ${repository.forksCount}")
                Text("Open issues (including PRs): ${repository.openIssuesCount}")
                Text("Default branch: ${repository.defaultBranch}")

                HorizontalDivider()

                Button(
                    onClick = onIssuesClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Issues")
                }

                Button(
                    onClick = onPullRequestsClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Pull Requests")
                }

                Button(
                    onClick = onCommitsClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Commits")
                }

                OutlinedButton(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(repository.htmlUrl)
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