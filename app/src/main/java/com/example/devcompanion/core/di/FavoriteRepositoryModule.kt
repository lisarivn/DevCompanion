package com.example.devcompanion.core.di

import com.example.devcompanion.data.repository.FavoritesRepositoryImpl
import com.example.devcompanion.domain.repository.FavoritesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class FavoritesRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindFavoritesRepository(
        implementation: FavoritesRepositoryImpl
    ): FavoritesRepository
}