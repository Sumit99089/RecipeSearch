package com.example.recipiesearch.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RecipieEntity(
    @PrimaryKey val id: Int,
    val image: String,
    val title: String,
    val readyInMinutes: Int
)
