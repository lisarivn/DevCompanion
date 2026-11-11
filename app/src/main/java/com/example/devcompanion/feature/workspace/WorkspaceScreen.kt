package com.example.devcompanion.feature.workspace

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.devcompanion.feature.auth.AuthContent
import com.example.devcompanion.feature.auth.AuthViewModel
import com.example.devcompanion.feature.profile.ProfileViewModel

@Composable
fun WorkspaceScreen(
    onAppearanceClick: () -> Unit,
    onAboutClick: () -> Unit,
    authViewModel: AuthViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    val isAuthenticated by
    profileViewModel.isAuthenticated.collectAsStateWithLifecycle()

    val myProfile by
    profileViewModel.myProfile.collectAsStateWithLifecycle()

    val myProfileLoading by
    profileViewModel.myProfileLoading.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Workspace",
                style = MaterialTheme.typography.headlineSmall
            )
        }

        item {
            Text(
                text = "Manage your account and application preferences.",
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // GitHub Account — перенесено з Dashboard
        item {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Your GitHub Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    AuthContent(viewModel = authViewModel)

                    if (isAuthenticated) {
                        if (myProfileLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.align(
                                    Alignment.CenterHorizontally
                                )
                            )
                        }

                        myProfile?.let { user ->
                            HorizontalDivider()

                            AsyncImage(
                                model = user.avatarUrl,
                                contentDescription = "GitHub profile avatar",
                                modifier = Modifier
                                    .size(80.dp)
                                    .clip(CircleShape)
                                    .align(Alignment.CenterHorizontally)
                            )

                            Text(
                                text = user.name ?: user.login,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.align(
                                    Alignment.CenterHorizontally
                                )
                            )

                            Text(
                                text = "@${user.login}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.align(
                                    Alignment.CenterHorizontally
                                )
                            )

                            user.bio
                                ?.takeIf { it.isNotBlank() }
                                ?.let { bio ->
                                    Text(
                                        text = bio,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }

                            HorizontalDivider()

                            ProfileStatistic(
                                label = "Public repositories",
                                value = user.publicRepos
                            )

                            ProfileStatistic(
                                label = "Followers",
                                value = user.followers
                            )

                            ProfileStatistic(
                                label = "Following",
                                value = user.following
                            )
                        }
                    }
                }
            }
        }

        item {
            WorkspaceItem(
                title = "Appearance",
                description = "Light, Dark and System theme",
                onClick = onAppearanceClick
            )
        }

        item {
            WorkspaceItem(
                title = "About DevCompanion",
                description = "Application information",
                onClick = onAboutClick
            )
        }
    }
}

@Composable
private fun WorkspaceItem(
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ProfileStatistic(
    label: String,
    value: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )

        Text(
            text = value.toString(),
            fontWeight = FontWeight.Bold
        )
    }
}
