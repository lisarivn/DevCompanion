package com.example.devcompanion.data.mapper

import com.example.devcompanion.data.local.entity.FavoriteRepositoryEntity
import com.example.devcompanion.domain.model.FavoriteRepository

fun FavoriteRepositoryEntity.toDomain(): FavoriteRepository {
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
        savedAt = savedAt
    )
}

fun FavoriteRepository.toEntity(): FavoriteRepositoryEntity {
    return FavoriteRepositoryEntity(
        id = id,
        name = name,
        fullName = fullName,
        ownerLogin = ownerLogin,
        description = description,
        language = language,
        starsCount = starsCount,
        forksCount = forksCount,
        htmlUrl = htmlUrl,
        savedAt = savedAt
    )
}