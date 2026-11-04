package com.example.devcompanion.feature.repositories

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.devcompanion.data.mapper.toFavoriteRepository
import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.feature.favorites.FavoritesViewModel
import androidx.compose.material3.OutlinedButton
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.mutableStateOf
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GitHubScreen(
    onRepositoryClick: (owner: String, repo: String) -> Unit,
    onFavoritesClick: () -> Unit,
    onUsersClick: () -> Unit,
    viewModel: GitHubViewModel = hiltViewModel(),
    favoritesViewModel: FavoritesViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val myRepositoriesState by viewModel.myRepositoriesState.collectAsState()
    val isAuthenticated by viewModel.isAuthenticated.collectAsState()

    val favorites by favoritesViewModel.favorites.collectAsState()

    val favoriteIds = remember(favorites) {
        favorites.map { it.id }.toSet()
    }

    var selectedTab by remember {
        mutableIntStateOf(0)
    }

    LaunchedEffect(selectedTab, isAuthenticated) {
        if (selectedTab == 1 && isAuthenticated) {
            viewModel.loadMyRepositories()
        }
    }

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

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onFavoritesClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = null
                )
                Text(
                    text = "Favorites",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }

            OutlinedButton(
                onClick = onUsersClick,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = Icons.Filled.Person,
                    contentDescription = null
                )
                Text(
                    text = "Search Users",
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
        }

        // Перемикач доступний в обох режимах.
        TabRow(selectedTabIndex = selectedTab) {
            Tab(
                selected = selectedTab == 0,
                onClick = { selectedTab = 0 },
                text = { Text("Search Repositories") }
            )

            Tab(
                selected = selectedTab == 1,
                onClick = { selectedTab = 1 },
                text = { Text("My Repositories") }
            )
        }

        if (selectedTab == 0) {
            // Публічний пошук репозиторіїв

            val languages = listOf(
                "All", "Kotlin", "Java", "Python",
                "JavaScript", "TypeScript", "C++", "C#"
            )

            var languageExpanded by remember {
                mutableStateOf(false)
            }

            val sortOptions = listOf(
                "Best match",
                "Stars",
                "Recently updated"
            )

            var sortExpanded by remember {
                mutableStateOf(false)
            }

            ExposedDropdownMenuBox(
                expanded = languageExpanded,
                onExpandedChange = { languageExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.language,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Language") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = languageExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = languageExpanded,
                    onDismissRequest = { languageExpanded = false }
                ) {
                    languages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                viewModel.updateLanguage(language)
                                languageExpanded = false
                            }
                        )
                    }
                }
            }

            ExposedDropdownMenuBox(
                expanded = sortExpanded,
                onExpandedChange = { sortExpanded = it }
            ) {
                OutlinedTextField(
                    value = uiState.sort,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sort by") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = sortExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = sortExpanded,
                    onDismissRequest = { sortExpanded = false }
                ) {
                    sortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateSort(option)
                                sortExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = uiState.query,
                onValueChange = viewModel::updateQuery,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Search repositories") },
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
                        isFavorite = repository.id in favoriteIds,
                        onFavoriteClick = {
                            favoritesViewModel.toggleFavorite(
                                repository = repository.toFavoriteRepository(),
                                isCurrentlyFavorite = repository.id in favoriteIds
                            )
                        },
                        onClick = {
                            val parts = repository.fullName.split(
                                "/",
                                limit = 2
                            )

                            if (parts.size == 2) {
                                onRepositoryClick(parts[0], parts[1])
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

        } else {
            // Власні репозиторії користувача

            val mySortOptions = listOf(
                "Recently updated",
                "Recently created",
                "Recently pushed",
                "Name (A-Z)"
            )

            val myLanguages = listOf(
                "All", "Kotlin", "Java", "Python",
                "JavaScript", "TypeScript", "C++", "C#"
            )

            var myLanguage by remember {
                mutableStateOf("All")
            }

            var myLanguageExpanded by remember {
                mutableStateOf(false)
            }

            ExposedDropdownMenuBox(
                expanded = myLanguageExpanded,
                onExpandedChange = { myLanguageExpanded = it }
            ) {
                OutlinedTextField(
                    value = myLanguage,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Language") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = myLanguageExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = myLanguageExpanded,
                    onDismissRequest = { myLanguageExpanded = false }
                ) {
                    myLanguages.forEach { language ->
                        DropdownMenuItem(
                            text = { Text(language) },
                            onClick = {
                                myLanguage = language
                                myLanguageExpanded = false
                            }
                        )
                    }
                }
            }

            var mySortExpanded by remember {
                mutableStateOf(false)
            }

            ExposedDropdownMenuBox(
                expanded = mySortExpanded,
                onExpandedChange = { mySortExpanded = it }
            ) {
                OutlinedTextField(
                    value = myRepositoriesState.sort,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Sort by") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = mySortExpanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = mySortExpanded,
                    onDismissRequest = { mySortExpanded = false }
                ) {
                    mySortOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                viewModel.updateMyRepositoriesSort(option)
                                mySortExpanded = false
                            }
                        )
                    }
                }
            }

            if (!isAuthenticated) {
                Text(
                    "Login to GitHub on Dashboard to view your repositories."
                )
            } else {
                if (myRepositoriesState.isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                myRepositoriesState.error?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error
                    )
                }

                if (
                    myRepositoriesState.hasLoaded &&
                    myRepositoriesState.repositories.isEmpty() &&
                    !myRepositoriesState.isLoading
                ) {
                    Text("No repositories found.")
                }

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(
                        items = myRepositoriesState.repositories.filter { repository ->
                            myLanguage == "All" ||
                                    repository.language.equals(
                                        myLanguage,
                                        ignoreCase = true
                                    )
                        },
                        key = { it.id }
                    ) { repository ->
                        RepositoryCard(
                            repository = repository,
                            isFavorite = repository.id in favoriteIds,
                            onFavoriteClick = {
                                favoritesViewModel.toggleFavorite(
                                    repository = repository.toFavoriteRepository(),
                                    isCurrentlyFavorite = repository.id in favoriteIds
                                )
                            },
                            onClick = {
                                val parts = repository.fullName.split(
                                    "/",
                                    limit = 2
                                )

                                if (parts.size == 2) {
                                    onRepositoryClick(parts[0], parts[1])
                                }
                            }
                        )
                    }

                    if (
                        myRepositoriesState.hasLoaded &&
                        myRepositoriesState.nextPage != null &&
                        !myRepositoriesState.isLoading
                    ) {
                        item(key = "my_load_more") {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (myRepositoriesState.isLoadingMore) {
                                    CircularProgressIndicator()
                                } else {
                                    Button(
                                        onClick = viewModel::loadMoreMyRepositories,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text(
                                            if (myRepositoriesState.error != null) {
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
    }
}


@Composable
private fun RepositoryCard(
    repository: GitHubRepository,
    isFavorite: Boolean,
    onFavoriteClick: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = repository.fullName,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onFavoriteClick
                ) {
                    Icon(
                        imageVector = if (isFavorite) {
                            Icons.Filled.Star
                        } else {
                            Icons.Outlined.StarBorder
                        },
                        contentDescription = if (isFavorite) {
                            "Remove from Favorites"
                        } else {
                            "Add to Favorites"
                        },
                        tint = if (isFavorite) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                }
            }

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