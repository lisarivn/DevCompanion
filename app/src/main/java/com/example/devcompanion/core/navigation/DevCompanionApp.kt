package com.example.devcompanion.core.navigation

import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.devcompanion.feature.dashboard.DashboardScreen
import com.example.devcompanion.feature.devtools.DevToolsScreen
import com.example.devcompanion.feature.favorites.FavoritesScreen
import com.example.devcompanion.feature.repositories.CommitsScreen
import com.example.devcompanion.feature.repositories.GitHubScreen
import com.example.devcompanion.feature.repositories.IssuesScreen
import com.example.devcompanion.feature.repositories.PullRequestsScreen
import com.example.devcompanion.feature.repositories.RepositoryDetailsScreen
import com.example.devcompanion.feature.workspace.WorkspaceScreen
import com.example.devcompanion.feature.devtools.json.JsonFormatterScreen
import com.example.devcompanion.feature.devtools.uuid.UuidGeneratorScreen
import com.example.devcompanion.feature.devtools.urlcodec.UrlCodecScreen
import com.example.devcompanion.feature.devtools.timestamp.TimestampConverterScreen
import com.example.devcompanion.feature.devtools.hash.HashGeneratorScreen
import com.example.devcompanion.feature.devtools.regex.RegexCheckerScreen
import com.example.devcompanion.feature.devtools.color.ColorConverterScreen

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DevCompanionApp() {

    val navController = rememberNavController()

    // Чотири головні вкладки застосунку
    val mainDestinations = listOf(
        AppDestination.Dashboard,
        AppDestination.GitHub,
        AppDestination.DevTools,
        AppDestination.Workspace
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    val isMainScreen = mainDestinations.any {
        it.route == currentRoute
    }

    // Заголовки вкладених екранів
    val screenTitle = when (currentRoute) {
        AppDestination.Favorites.route -> "Favorites"
        AppDestination.JsonFormatter.route -> "JSON Formatter"
        AppDestination.RepositoryDetails.route -> "Repository Details"
        AppDestination.Issues.route -> "Issues"
        AppDestination.PullRequests.route -> "Pull Requests"
        AppDestination.Commits.route -> "Commits"
        AppDestination.UuidGenerator.route -> "UUID Generator"
        AppDestination.UrlCodec.route -> "URL Encoder / Decoder"
        AppDestination.Timestamp.route -> "Timestamp Converter"
        AppDestination.HashGenerator.route -> "Hash Generator"
        AppDestination.RegexChecker.route -> "Regex Checker"
        AppDestination.ColorConverter.route -> "Color Converter"
        else -> "DevCompanion"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        topBar = {
            if (!isMainScreen) {
                TopAppBar(
                    title = {
                        Text(screenTitle)
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                navController.popBackStack()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                )
            }
        },

        bottomBar = {
            if (isMainScreen) {
                NavigationBar {
                    mainDestinations.forEach { destination ->
                        val icon = when (destination) {
                            AppDestination.Dashboard -> Icons.Default.Home
                            AppDestination.GitHub -> Icons.Default.List
                            AppDestination.DevTools -> Icons.Default.Build
                            AppDestination.Workspace -> Icons.Default.Folder
                            else -> Icons.Default.Home
                        }

                        NavigationBarItem(
                            selected = currentRoute == destination.route,

                            onClick = {
                                navController.navigate(destination.route) {
                                    popUpTo(
                                        navController.graph
                                            .findStartDestination().id
                                    ) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },

                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = destination.title
                                )
                            },

                            label = {
                                Text(destination.title)
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->

        NavHost(
            navController = navController,
            startDestination = AppDestination.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {

            // Dashboard
            composable(AppDestination.Dashboard.route) {
                DashboardScreen()
            }

            // GitHub Explorer
            composable(AppDestination.GitHub.route) {
                GitHubScreen(
                    onRepositoryClick = { owner, repo ->
                        navController.navigate(
                            AppDestination.RepositoryDetails
                                .createRoute(owner, repo)
                        )
                    },
                    onFavoritesClick = {
                        navController.navigate(
                            AppDestination.Favorites.route
                        )
                    }
                )
            }

            // Favorites
            composable(AppDestination.Favorites.route) {
                FavoritesScreen(
                    onRepositoryClick = { owner, repo ->
                        navController.navigate(
                            AppDestination.RepositoryDetails
                                .createRoute(owner, repo)
                        )
                    }
                )
            }

            // Repository Details
            composable(
                route = AppDestination.RepositoryDetails.route,
                arguments = listOf(
                    navArgument("owner") {
                        type = NavType.StringType
                    },
                    navArgument("repo") {
                        type = NavType.StringType
                    }
                )
            ) { entry ->

                val owner = entry.arguments
                    ?.getString("owner")
                    .orEmpty()

                val repo = entry.arguments
                    ?.getString("repo")
                    .orEmpty()

                val repositoryPath =
                    "repository/${Uri.encode(owner)}/${Uri.encode(repo)}"

                RepositoryDetailsScreen(
                    onIssuesClick = {
                        navController.navigate(
                            "$repositoryPath/issues"
                        )
                    },

                    onPullRequestsClick = {
                        navController.navigate(
                            "$repositoryPath/pulls"
                        )
                    },

                    onCommitsClick = {
                        navController.navigate(
                            "$repositoryPath/commits"
                        )
                    }
                )
            }

            // Issues
            composable(AppDestination.Issues.route) { entry ->
                val owner = entry.arguments?.getString("owner").orEmpty()
                val repo = entry.arguments?.getString("repo").orEmpty()

                IssuesScreen(owner = owner, repo = repo)
            }

            // Pull Requests
            composable(AppDestination.PullRequests.route) { entry ->
                val owner = entry.arguments?.getString("owner").orEmpty()
                val repo = entry.arguments?.getString("repo").orEmpty()

                PullRequestsScreen(owner = owner, repo = repo)
            }

            // Commits
            composable(AppDestination.Commits.route) { entry ->
                val owner = entry.arguments?.getString("owner").orEmpty()
                val repo = entry.arguments?.getString("repo").orEmpty()

                CommitsScreen(owner = owner, repo = repo)
            }

            // DevTools
            composable(AppDestination.DevTools.route) {
                DevToolsScreen(
                    onToolClick = { toolId ->
                        when (toolId) {
                            "json" -> {
                                navController.navigate(
                                    AppDestination.JsonFormatter.route
                                )
                            }
                            "uuid" -> navController.navigate(
                                AppDestination.UuidGenerator.route
                            )

                            "urlcodec" -> navController.navigate(
                                AppDestination.UrlCodec.route
                            )

                            "timestamp" -> navController.navigate(
                                AppDestination.Timestamp.route
                            )

                            "hash" -> navController.navigate(
                                AppDestination.HashGenerator.route
                            )

                            "regex" -> navController.navigate(
                                AppDestination.RegexChecker.route
                            )

                            "color" -> navController.navigate(
                                AppDestination.ColorConverter.route
                            )
                        }
                    }
                )
            }

            composable(AppDestination.JsonFormatter.route) {
                JsonFormatterScreen()
            }

            composable(AppDestination.UuidGenerator.route) {
                UuidGeneratorScreen()
            }

            composable(AppDestination.UrlCodec.route) {
                UrlCodecScreen()
            }

            composable(AppDestination.Timestamp.route) {
                TimestampConverterScreen()
            }

            composable(AppDestination.HashGenerator.route) {
                HashGeneratorScreen()
            }

            composable(AppDestination.RegexChecker.route) {
                RegexCheckerScreen()
            }

            composable(AppDestination.ColorConverter.route) {
                ColorConverterScreen()
            }

            // Workspace
            composable(AppDestination.Workspace.route) {
                WorkspaceScreen()
            }
        }
    }
}
