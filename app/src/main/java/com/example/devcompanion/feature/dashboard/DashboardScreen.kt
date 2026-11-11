package com.example.devcompanion.feature.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.devcompanion.domain.model.FavoriteRepository
import com.example.devcompanion.feature.profile.ProfileViewModel
import androidx.compose.runtime.LaunchedEffect

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val isAuthenticated by profileViewModel.isAuthenticated.collectAsStateWithLifecycle()
    val myProfile by profileViewModel.myProfile.collectAsStateWithLifecycle()

    val events by viewModel.events.collectAsStateWithLifecycle()
    val eventsLoading by viewModel.eventsLoading.collectAsStateWithLifecycle()
    val eventsError by viewModel.eventsError.collectAsStateWithLifecycle()

    LaunchedEffect(isAuthenticated, myProfile?.login) {
        if (isAuthenticated && myProfile != null) {
            viewModel.loadEvents(myProfile!!.login)
        } else {
            viewModel.clearEvents()
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // Saved repositories
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "Saved repositories",
                            style = MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = uiState.savedRepositoriesCount.toString(),
                            style = MaterialTheme.typography.headlineLarge
                        )
                    }

                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // Recent favorites
        item {
            Text(
                text = "Recent favorites",
                style = MaterialTheme.typography.titleLarge
            )
        }

        if (uiState.recentFavorites.isEmpty()) {
            item {
                Text(
                    text = "No saved repositories yet.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(
                items = uiState.recentFavorites,
                key = { it.id }
            ) { repository ->
                RecentFavoriteCard(repository)
            }
        }

        item {
            Text(
                text = "Recent GitHub Activity",
                style = MaterialTheme.typography.titleLarge
            )
        }

        when {
            !isAuthenticated -> {
                item {
                    Text("Login to view your GitHub activity.")
                }
            }

            myProfile == null -> {
                item {
                    Text("Waiting for GitHub profile...")
                }
            }

            eventsLoading -> {
                item {
                    CircularProgressIndicator()
                }
            }

            eventsError != null -> {
                item {
                    Text(
                        text = eventsError ?: "",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            events.isEmpty() -> {
                item {
                    Text("No recent public activity found.")
                }
            }

            else -> {
                items(
                    items = events,
                    key = { "event_${it.id}" }
                ) { event ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = event.description,
                                style = MaterialTheme.typography.titleMedium
                            )

                            Text(event.repositoryName)

                            Text(
                                text = event.createdAt,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun RecentFavoriteCard(
    repository: FavoriteRepository
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = repository.fullName,
                style = MaterialTheme.typography.titleMedium
            )

            repository.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Text(
                text = "Language: ${repository.language ?: "Unknown"}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
