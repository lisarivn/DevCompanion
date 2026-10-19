package com.example.devcompanion.data.mapper

import com.example.devcompanion.data.remote.dto.GitHubRepositoryDto
import com.example.devcompanion.domain.model.GitHubRepository

fun GitHubRepositoryDto.toDomain(): GitHubRepository {
    return GitHubRepository(
        id = id,
        fullName = fullName,
        description = description,
        language = language,
        htmlUrl = htmlUrl,
        starsCount = starsCount
    )
}