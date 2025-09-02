package com.example.recipiesearch.data.remote

import com.example.recipiesearch.data.remote.dto.RandomRecipeResponse
import com.example.recipiesearch.data.remote.dto.RecipeSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipieSearchApi {
    @GET("random")
    suspend fun getPopularRecipies(
        @Query("apiKey") apiKey: String = API_KEY,
        @Query("number") number: Int = 10 // To get more than one popular recipe
    ): RandomRecipeResponse

    @GET("complexSearch")
    suspend fun getAllRecipies(
        @Query("query") query: String,
        @Query("apiKey") apiKey: String = API_KEY
    ): RecipeSearchResponse


    companion object {
        const val API_KEY = "490056c33b9e4427abfde1a78cb9c4cb"
        const val BASE_URL = "https://api.spoonacular.com/recipes/"
    }
}