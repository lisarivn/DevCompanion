package com.example.devcompanion.domain.repository

import com.example.devcompanion.domain.model.FavoriteRepository
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {

    fun getAllFavorites(): Flow<List<FavoriteRepository>>

    fun isFavorite(repositoryId: Long): Flow<Boolean>

    fun getFavoritesCount(): Flow<Int>

    suspend fun addFavorite(repository: FavoriteRepository)

    suspend fun removeFavorite(repositoryId: Long)
}