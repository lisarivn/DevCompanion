package com.example.devcompanion.feature.profile

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.devcompanion.domain.model.GitHubUser

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var username by rememberSaveable {
        mutableStateOf("")
    }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "GitHub Profile",
            style = MaterialTheme.typography.headlineMedium
        )

        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("GitHub username") },
            placeholder = { Text("e.g. octocat") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    viewModel.loadProfile(username)
                }
            )
        )

        Button(
            onClick = {
                viewModel.loadProfile(username)
            },
            enabled = username.isNotBlank() &&
                    uiState !is ProfileUiState.Loading,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Search")
        }

        when (val state = uiState) {
            ProfileUiState.Idle -> {
                Text(
                    text = "Enter a GitHub username to view their profile.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            ProfileUiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }

            is ProfileUiState.Success -> {
                ProfileContent(
                    user = state.user,
                    onOpenGitHub = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse(state.user.htmlUrl)
                        )
                        context.startActivity(intent)
                    }
                )
            }

            is ProfileUiState.Error -> {
                Text(
                    text = state.message,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun ProfileContent(
    user: GitHubUser,
    onOpenGitHub: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            AsyncImage(
                model = user.avatarUrl,
                contentDescription = "GitHub avatar",
                modifier = Modifier
                    .size(96.dp)
                    .clip(CircleShape)
                    .align(Alignment.CenterHorizontally)
            )

            Text(
                text = user.name ?: user.login,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Text(
                text = "@${user.login}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            user.bio
                ?.takeIf { it.isNotBlank() }
                ?.let { bio ->
                    Text(
                        text = bio,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

            HorizontalDivider()

            Text(
                text = "Statistics",
                style = MaterialTheme.typography.titleMedium
            )

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

            HorizontalDivider()

            user.company
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    ProfileDetail("Company", it)
                }

            user.location
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    ProfileDetail("Location", it)
                }

            user.blog
                ?.takeIf { it.isNotBlank() }
                ?.let {
                    ProfileDetail("Website", it)
                }

            ProfileDetail(
                label = "Joined GitHub",
                value = user.createdAt.substringBefore("T")
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onOpenGitHub,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open on GitHub")
            }
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

@Composable
private fun ProfileDetail(
    label: String,
    value: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}