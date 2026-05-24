package com.example.dogfinder.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorites")
data class FavoriteDog(
    @PrimaryKey val imageUrl: String,
    val breed: String
)