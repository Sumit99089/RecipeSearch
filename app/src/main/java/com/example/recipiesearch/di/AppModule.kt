package com.example.recipiesearch.di

import com.example.recipiesearch.data.remote.RecipieSearchApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton
import android.app.Application
import androidx.room.Room
import com.example.recipiesearch.data.local.RecipieDao
import com.example.recipiesearch.data.local.RecipieDatabase
import retrofit2.converter.gson.GsonConverterFactory


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRecipieSearchApi(): RecipieSearchApi {
        return Retrofit.Builder()
            .baseUrl(RecipieSearchApi.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BASIC }).build())
            .build()
            .create(RecipieSearchApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRecipieDatabase(app: Application): RecipieDatabase {
        return Room.databaseBuilder(
            app,
            RecipieDatabase::class.java,
            "stockdb.db"
        ).build()
    }

}