package com.example.devcompanion.data.repository

import com.example.devcompanion.data.local.dao.FavoriteRepositoryDao
import com.example.devcompanion.data.mapper.toDomain
import com.example.devcompanion.data.mapper.toEntity
import com.example.devcompanion.domain.model.FavoriteRepository
import com.example.devcompanion.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteRepositoryDao: FavoriteRepositoryDao
) : FavoritesRepository {

    override fun getAllFavorites(): Flow<List<FavoriteRepository>> {
        return favoriteRepositoryDao.getAllFavorites()
            .map { entities ->
                entities.map { entity -> entity.toDomain() }
            }
    }

    override fun isFavorite(repositoryId: Long): Flow<Boolean> {
        return favoriteRepositoryDao.isFavorite(repositoryId)
    }

    override fun getFavoritesCount(): Flow<Int> {
        return favoriteRepositoryDao.getFavoritesCount()
    }

    override suspend fun addFavorite(repository: FavoriteRepository) {
        favoriteRepositoryDao.insertFavorite(repository.toEntity())
    }

    override suspend fun removeFavorite(repositoryId: Long) {
        favoriteRepositoryDao.deleteFavorite(repositoryId)
    }
}