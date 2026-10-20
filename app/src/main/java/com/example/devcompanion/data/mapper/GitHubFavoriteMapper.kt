
package com.example.devcompanion.data.mapper

import com.example.devcompanion.domain.model.FavoriteRepository
import com.example.devcompanion.domain.model.GitHubRepository
import com.example.devcompanion.domain.model.GitHubRepositoryDetails

fun GitHubRepositoryDetails.toFavoriteRepository(): FavoriteRepository {
    return FavoriteRepository(
        id = id,
        name = name,
        fullName = fullName,
        ownerLogin = ownerLogin,
        description = description,
        language = language,
        starsCount = starsCount,
        forksCount = forksCount,
        htmlUrl = htmlUrl,
        savedAt = System.currentTimeMillis()
    )
}

fun GitHubRepository.toFavoriteRepository(): FavoriteRepository {
    val parts = fullName.split("/", limit = 2)

    return FavoriteRepository(
        id = id,
        name = parts.getOrElse(1) { fullName },
        fullName = fullName,
        ownerLogin = parts.first(),
        description = description,
        language = language,
        starsCount = starsCount,
        forksCount = 0,
        htmlUrl = htmlUrl,
        savedAt = System.currentTimeMillis()
    )
}
