package com.example.dogfinder.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow
// Room se encarga de traducir estas funciones a código SQL automáticamente.
@Dao
interface DogDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavorite(dog: FavoriteDog)

    @Delete
    fun deleteFavorite(dog: FavoriteDog)


    @Query("SELECT * FROM favorites")
    fun getAllFavorites(): Flow<List<FavoriteDog>>


    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE imageUrl = :url)")
    fun isFavorite(url: String): Flow<Boolean>
}