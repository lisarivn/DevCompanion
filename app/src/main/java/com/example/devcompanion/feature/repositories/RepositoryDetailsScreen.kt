
package com.example.devcompanion.feature.repositories

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devcompanion.data.mapper.toFavoriteRepository
import com.example.devcompanion.feature.favorites.FavoritesViewModel

@Composable
fun RepositoryDetailsScreen(
    onIssuesClick: () -> Unit,
    onPullRequestsClick: () -> Unit,
    onCommitsClick: () -> Unit,
    viewModel: RepositoryDetailsViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
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

            val favoriteFlow = remember(repository.id, favoritesViewModel) {
                favoritesViewModel.isFavorite(repository.id)
            }
            val isFavorite by favoriteFlow.collectAsState(initial = false)

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

                OutlinedButton(
                    onClick = {
                        favoritesViewModel.toggleFavorite(
                            repository = repository.toFavoriteRepository(),
                            isCurrentlyFavorite = isFavorite
                        )
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isFavorite) {
                                Icons.Filled.Star
                            } else {
                                Icons.Outlined.StarBorder
                            },
                            contentDescription = null
                        )

                        Text(
                            text = if (isFavorite) {
                                "Remove from Favorites"
                            } else {
                                "Add to Favorites"
                            }
                        )
                    }
                }

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
