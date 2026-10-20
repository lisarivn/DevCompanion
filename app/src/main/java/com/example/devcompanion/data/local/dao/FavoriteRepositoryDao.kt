package com.example.devcompanion.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.devcompanion.data.local.entity.FavoriteRepositoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteRepositoryDao {

    @Query(
        "SELECT * FROM favorite_repositories ORDER BY savedAt DESC"
    )
    fun getAllFavorites(): Flow<List<FavoriteRepositoryEntity>>

    @Query(
        "SELECT EXISTS(SELECT 1 FROM favorite_repositories WHERE id = :repositoryId)"
    )
    fun isFavorite(repositoryId: Long): Flow<Boolean>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(repository: FavoriteRepositoryEntity)

    @Query(
        "DELETE FROM favorite_repositories WHERE id = :repositoryId"
    )
    suspend fun deleteFavorite(repositoryId: Long)

    @Query("SELECT COUNT(*) FROM favorite_repositories")
    fun getFavoritesCount(): Flow<Int>
}