package com.example.recipiesearch.domain.repository

import com.example.recipiesearch.domain.model.Recipie
import com.example.recipiesearch.util.Resource
import kotlinx.coroutines.flow.Flow

interface RecipieRepository {

    suspend fun getAllRecipies(
        query: String
    ): Flow<Resource<List<Recipie>>>

    suspend fun getPopularRecipies(): Flow<Resource<List<Recipie>>>

    suspend fun getFavouriteRecipies(): Flow<Resource<List<Recipie>>>

    suspend fun insertFavouriteRecipie(recipie: Recipie)

    suspend fun clearFavouriteRecipie(id: Int)

}