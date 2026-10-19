package com.example.devcompanion.core.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.devcompanion.feature.dashboard.DashboardScreen
import com.example.devcompanion.feature.repositories.GitHubScreen
import com.example.devcompanion.feature.devtools.DevToolsScreen
import com.example.devcompanion.feature.workspace.WorkspaceScreen

@Composable
fun DevCompanionApp() {
    val navController = rememberNavController()

    val destinations = listOf(
        AppDestination.Dashboard,
        AppDestination.GitHub,
        AppDestination.DevTools,
        AppDestination.Workspace
    )

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar {
                destinations.forEach { destination ->
                    val icon = when (destination) {
                        AppDestination.Dashboard -> Icons.Default.Home
                        AppDestination.GitHub -> Icons.Default.List
                        AppDestination.DevTools -> Icons.Default.Build
                        AppDestination.Workspace -> Icons.Default.Folder
                    }

                    NavigationBarItem(
                        selected = currentRoute == destination.route,
                        onClick = {
                            navController.navigate(destination.route) {
                                popUpTo(
                                    navController.graph.findStartDestination().id
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.Dashboard.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppDestination.Dashboard.route) {
                DashboardScreen()
            }

            composable(AppDestination.GitHub.route) {
                GitHubScreen()
            }

            composable(AppDestination.DevTools.route) {
                DevToolsScreen()
            }

            composable(AppDestination.Workspace.route) {
                WorkspaceScreen()
            }
        }
    }
}