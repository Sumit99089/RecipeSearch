package com.example.recipiesearch.data.remote

import com.example.recipiesearch.data.remote.dto.RecipieDto
import retrofit2.http.GET
import retrofit2.http.Query

interface RecipieSearchApi {
    @GET("recipes/random")
    suspend fun getPopularRecipies(
        @Query("apiKey") apiKey: String = API_KEY): List<RecipieDto>

    @GET("recipes/complexSearch")
    suspend fun getAllRecipies(
        @Query("query") query: String,
        @Query("apiKey") apiKey: String = API_KEY): List<RecipieDto>


    companion object {
        const val API_KEY = "490056c33b9e4427abfde1a78cb9c4cb"
        const val BASE_URL = "https://api.spoonacular.com"
    }
}