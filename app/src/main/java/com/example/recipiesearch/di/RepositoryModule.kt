package com.example.recipiesearch.di

import com.example.recipiesearch.data.repository.RecipieRepositoryImpl
import com.example.recipiesearch.domain.repository.RecipieRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindRecipieRepository(
        recipieRepositoryImpl: RecipieRepositoryImpl
    ): RecipieRepository
}


