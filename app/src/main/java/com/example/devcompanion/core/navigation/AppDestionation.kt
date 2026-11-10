
package com.example.devcompanion.core.navigation

import android.net.Uri

sealed class AppDestination(
    val route: String,
    val title: String
) {

    data object Dashboard : AppDestination("dashboard", "Dashboard")
    data object GitHub : AppDestination("github", "GitHub")
    data object Favorites : AppDestination("favorites", "Favorites")
    data object Profile : AppDestination(route = "profile", title = "GitHub Profile")
    data object DevTools : AppDestination("devtools", "DevTools")
    data object Workspace : AppDestination("workspace", "Workspace")

    data object RepositoryDetails : AppDestination(
        "repository/{owner}/{repo}",
        "Repository"
    ) {
        fun createRoute(owner: String, repo: String): String {
            return "repository/${Uri.encode(owner)}/${Uri.encode(repo)}"
        }
    }

    data object Issues : AppDestination(
        "repository/{owner}/{repo}/issues",
        "Issues"
    )

    data object IssueDetails : AppDestination(
        "repository/{owner}/{repo}/issues/{issueNumber}",
        "Issue Details"
    ) {
        fun createRoute(
            owner: String,
            repo: String,
            issueNumber: Int
        ): String =
            "repository/${Uri.encode(owner)}/${Uri.encode(repo)}/issues/$issueNumber"
    }

    data object PullRequests : AppDestination(
        "repository/{owner}/{repo}/pulls",
        "Pull Requests"
    )

    data object Commits : AppDestination(
        "repository/{owner}/{repo}/commits",
        "Commits"
    )

    data object CommitDetails : AppDestination(
        "repository/{owner}/{repo}/commits/{sha}",
        "Commit Details"
    ) {
        fun createRoute(
            owner: String,
            repo: String,
            sha: String
        ): String {
            return "repository/${Uri.encode(owner)}/${Uri.encode(repo)}/commits/${Uri.encode(sha)}"
        }
    }

    data object PullRequestDetails : AppDestination(
        "repository/{owner}/{repo}/pulls/{pullNumber}",
        "Pull Request Details"
    ) {
        fun createRoute(
            owner: String,
            repo: String,
            pullNumber: Int
        ): String {
            return "repository/${Uri.encode(owner)}/${Uri.encode(repo)}/pulls/$pullNumber"
        }
    }



    data object JsonFormatter : AppDestination(
        route = "devtools/json",
        title = "JSON Formatter"
    )

    data object UuidGenerator : AppDestination(
        route = "devtools/uuid",
        title = "UUID Generator"
    )

    data object Timestamp : AppDestination(
        route = "devtools/timestamp",
        title = "Timestamp Converter"
    )

    data object RegexChecker: AppDestination(
        route = "devtools/regex",
        title = "Regex Checker"
    )

    data object HttpClient : AppDestination(
        route = "devtools/http",
        title = "HTTP Client"
    )

}
