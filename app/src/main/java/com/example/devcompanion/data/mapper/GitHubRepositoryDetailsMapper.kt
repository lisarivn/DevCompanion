package com.example.devcompanion.data.mapper

import com.example.devcompanion.data.remote.dto.GitHubRepositoryDetailsDto
import com.example.devcompanion.domain.model.GitHubRepositoryDetails

fun GitHubRepositoryDetailsDto.toDomain(): GitHubRepositoryDetails {
    return GitHubRepositoryDetails(
        id = id,
        fullName = fullName,
        name = name,
        description = description,
        language = language,
        htmlUrl = htmlUrl,
        starsCount = starsCount,
        forksCount = forksCount,
        openIssuesCount = openIssuesCount,
        defaultBranch = defaultBranch,
        ownerLogin = owner.login,
        ownerAvatarUrl = owner.avatarUrl
    )
}