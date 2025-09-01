package com.example.recipiesearch.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface RecipieDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavouriteRecipie(
        favouriteRecipieEntity: RecipieEntity
    )

    @Query("DELETE FROM recipieentity WHERE id = :id")
    suspend fun clearFavouriteRecipie( id: Int)

    @Query("SELECT * FROM recipieentity")
    suspend fun getAllFavourites(): List<RecipieEntity>

    @Query("SELECT id FROM recipieentity")
    suspend fun getAllFavouritesIds(): List<Int>
}