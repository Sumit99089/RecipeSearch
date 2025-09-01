package com.example.recipiesearch.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [RecipieEntity::class],
    version = 1
)
abstract class RecipieDatabase: RoomDatabase() {
    abstract val dao: RecipieDao
}