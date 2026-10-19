package com.example.devcompanion.core.navigation

sealed class AppDestination(
    val route: String,
    val title: String
) {
    data object Dashboard : AppDestination(
        route = "dashboard",
        title = "Dashboard"
    )

    data object GitHub : AppDestination(
        route = "github",
        title = "GitHub"
    )

    data object DevTools : AppDestination(
        route = "devtools",
        title = "DevTools"
    )

    data object Workspace : AppDestination(
        route = "workspace",
        title = "Workspace"
    )
}