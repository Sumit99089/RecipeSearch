package com.example.recipiesearch.data.repository

import coil.network.HttpException
import com.example.recipiesearch.data.local.RecipieDao
import com.example.recipiesearch.data.mapper.toRecipie
import com.example.recipiesearch.data.mapper.toRecipieEntity
import com.example.recipiesearch.data.remote.RecipieSearchApi
import com.example.recipiesearch.domain.model.Recipie
import com.example.recipiesearch.domain.repository.RecipieRepository
import com.example.recipiesearch.util.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RecipieRepositoryImpl @Inject constructor(
    val api: RecipieSearchApi,
    val dao: RecipieDao
) : RecipieRepository {


    override suspend fun getAllRecipies(query: String): Flow<Resource<List<Recipie>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val remoteRecipes = api.getAllRecipies(query)
                val favouriteIds = dao.getAllFavouritesIds() // local cache

                val recipes = remoteRecipes.map { dto ->
                    dto.toRecipie().copy(
                        isFavourite = favouriteIds.contains(dto.id)
                    )
                }

                emit(Resource.Success(recipes))
            } catch (e: IOException) {
                emit(Resource.Error("Couldn't reach server. Check internet."))
            } catch (e: HttpException) {
                emit(Resource.Error("Server error: ${e.response.code}"))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun getPopularRecipies(): Flow<Resource<List<Recipie>>> {
        return flow {
            emit(Resource.Loading(true))

            val apiResponse = try {
                api.getPopularRecipies()
            } catch (e: HttpException) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load data: ${e.localizedMessage ?: "Unknown error"}"))
                null
            } catch (e: IOException) { // important: handle network errors too
                e.printStackTrace()
                emit(Resource.Error("Couldn't reach server. Check your internet connection."))
                null
            }

            apiResponse?.let {
                emit(Resource.Success(it.map { it -> it.toRecipie() }))
            }

            emit(Resource.Loading(false))
        }
    }

    override suspend fun getFavouriteRecipies(): Flow<Resource<List<Recipie>>> {
        return flow {
            emit(Resource.Loading(true))
            try {
                val favourites = dao.getAllFavourites()
                emit(Resource.Success(favourites.map { entity -> entity.toRecipie() }))
            } catch (e: Exception) {
                e.printStackTrace()
                emit(Resource.Error("Couldn't load favourites: ${e.localizedMessage ?: "Unknown error"}"))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }


    override suspend fun insertFavouriteRecipie(recipie: Recipie) {
        try{
            dao.insertFavouriteRecipie(favouriteRecipieEntity = recipie.toRecipieEntity())
        }catch(e: Exception){
            e.printStackTrace()
        }
    }

    override suspend fun clearFavouriteRecipie(id: Int) {
        try{
            dao.clearFavouriteRecipie(id = id)
        }catch(e: Exception){
            e.printStackTrace()
        }
    }
}