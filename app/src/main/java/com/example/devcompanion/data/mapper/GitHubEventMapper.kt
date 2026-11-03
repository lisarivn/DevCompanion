package com.example.devcompanion.data.mapper

import com.example.devcompanion.data.remote.dto.GitHubEventDto
import com.example.devcompanion.domain.model.GitHubEvent

fun GitHubEventDto.toDomain(): GitHubEvent {
    val description = when (type) {
        "PushEvent" -> "Pushed commits"
        "PullRequestEvent" -> "Pull request activity"
        "IssuesEvent" -> "Issue activity"
        "CreateEvent" -> "Created a branch or tag"
        "ForkEvent" -> "Forked a repository"
        "WatchEvent" -> "Starred a repository"
        "ReleaseEvent" -> "Published a release"
        else -> type.removeSuffix("Event")
    }

    return GitHubEvent(
        id = id,
        type = type,
        repositoryName = repo.name,
        createdAt = createdAt,
        description = description
    )
}