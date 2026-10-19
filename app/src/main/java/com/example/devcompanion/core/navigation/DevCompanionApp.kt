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
import com.example.devcompanion.feature.repositories.GitHubScreen
import com.example.devcompanion.feature.repositories.RepositoryDetailsScreen
import com.example.devcompanion.feature.workspace.WorkspaceScreen

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

    // Визначаємо, чи перебуваємо на головному екрані
    val isMainScreen = mainDestinations.any {
        it.route == currentRoute
    }

    // Назва для верхньої панелі вкладеного екрана
    val screenTitle = when (currentRoute) {
        AppDestination.RepositoryDetails.route -> "Repository Details"
        AppDestination.Issues.route -> "Issues"
        AppDestination.PullRequests.route -> "Pull Requests"
        AppDestination.Commits.route -> "Commits"
        else -> "DevCompanion"
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),

        // Верхня панель лише для вкладених екранів
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

        // Нижня панель лише для головних вкладок
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
            composable(
                route = AppDestination.Issues.route,
                arguments = listOf(
                    navArgument("owner") {
                        type = NavType.StringType
                    },
                    navArgument("repo") {
                        type = NavType.StringType
                    }
                )
            ) {
                Text("Issues screen")
            }

            // Pull Requests
            composable(
                route = AppDestination.PullRequests.route,
                arguments = listOf(
                    navArgument("owner") {
                        type = NavType.StringType
                    },
                    navArgument("repo") {
                        type = NavType.StringType
                    }
                )
            ) {
                Text("Pull Requests screen")
            }

            // Commits
            composable(
                route = AppDestination.Commits.route,
                arguments = listOf(
                    navArgument("owner") {
                        type = NavType.StringType
                    },
                    navArgument("repo") {
                        type = NavType.StringType
                    }
                )
            ) {
                Text("Commits screen")
            }

            // DevTools
            composable(AppDestination.DevTools.route) {
                DevToolsScreen()
            }

            // Workspace
            composable(AppDestination.Workspace.route) {
                WorkspaceScreen()
            }
        }
    }
}