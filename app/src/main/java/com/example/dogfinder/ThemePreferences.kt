package com.example.dogfinder

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Crea el DataStore. Es como un "cajón" donde se guardan las preferencias del usuario.
// El nombre "settings" es el archivo donde se almacenan en el celular.
private val Context.dataStore by preferencesDataStore(name = "settings")

class ThemePreferences(private val context: Context) {

    companion object {
        // La "llave" con la que guardamos/leemos el modo oscuro.
        // Es un booleano: true = modo oscuro activado, false = modo claro.
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
    }

    // Flow que emite el valor actual del modo oscuro.
    // Si nunca se guardó nada, devuelve false (modo claro por defecto).
    // Se actualiza automáticamente cada vez que cambia el valor.
    val isDarkMode: Flow<Boolean> = context.dataStore.data
        .map { preferences -> preferences[DARK_MODE_KEY] ?: false }

    // Guarda el nuevo valor del modo oscuro.
    // Es suspend porque escribir en disco es asíncrono.
    suspend fun setDarkMode(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[DARK_MODE_KEY] = enabled
        }
    }
}
