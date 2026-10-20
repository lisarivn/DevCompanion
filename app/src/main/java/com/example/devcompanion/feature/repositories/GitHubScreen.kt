package com.example.devcompanion.feature.repositories

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devcompanion.domain.model.GitHubRepository

@Composable
fun GitHubScreen(
    onRepositoryClick: (owner: String, repo: String) -> Unit,
    viewModel: GitHubViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Text(
            text = "GitHub Explorer",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = uiState.query,
            onValueChange = viewModel::updateQuery,
            modifier = Modifier.fillMaxWidth(),
            label = {
                Text("Search repositories")
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.searchRepositories()
                }
            )
        )

        Button(
            onClick = viewModel::searchRepositories,
            enabled = !uiState.isLoading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        if (uiState.isLoading) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                CircularProgressIndicator()
            }
        }

        uiState.error?.let { error ->
            Text(
                text = error,
                color = MaterialTheme.colorScheme.error
            )
        }

        if (
            uiState.hasSearched &&
            !uiState.isLoading &&
            uiState.error == null &&
            uiState.repositories.isEmpty() &&
            uiState.nextPage == null
        ) {
            Text("No repositories found.")
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            items(
                items = uiState.repositories,
                key = { it.id }
            ) { repository ->

                RepositoryCard(
                    repository = repository,
                    onClick = {
                        val parts = repository.fullName.split(
                            "/",
                            limit = 2
                        )

                        if (parts.size == 2) {
                            onRepositoryClick(
                                parts[0],
                                parts[1]
                            )
                        }
                    }
                )
            }

            if (
                uiState.hasSearched &&
                uiState.nextPage != null &&
                !uiState.isLoading
            ) {
                item(key = "load_more") {

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        if (uiState.isLoadingMore) {
                            CircularProgressIndicator()
                        } else {
                            Button(
                                onClick = viewModel::loadMoreRepositories,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (uiState.error != null) {
                                        "Retry loading"
                                    } else {
                                        "Load more"
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RepositoryCard(
    repository: GitHubRepository,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            Text(
                text = repository.fullName,
                style = MaterialTheme.typography.titleMedium
            )

            repository.description?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Language: ${repository.language ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall
            )

            Text(
                text = "Stars: ${repository.starsCount}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}