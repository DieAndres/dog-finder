package com.example.dogfinder.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavoriteDog::class], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun dogDao(): DogDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        // Aca enciendo la base de datos "dog_database" si no lo esta
        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "dog_database"
                ).build()
            }
            return INSTANCE!!
        }
    }
}