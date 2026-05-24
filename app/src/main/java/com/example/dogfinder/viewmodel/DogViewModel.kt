package com.example.dogfinder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogfinder.data.api.RetrofitInstance
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.example.dogfinder.data.local.AppDatabase
import com.example.dogfinder.data.local.FavoriteDog
/*HOOK*/
class DogViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val dogDao = db.dogDao()
    private val _breeds = mutableStateOf<List<String>>(emptyList())
    val breeds: State<List<String>> = _breeds
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage
    private val _breedImages = mutableStateOf<List<String>>(emptyList())
    val breedImages: State<List<String>> = _breedImages
    private val _favorites = mutableStateOf<List<FavoriteDog>>(emptyList())
    val favorites: State<List<FavoriteDog>> = _favorites
    //ejecutar automáticamente cuando se crea el ViewModel
    init {
        getBreeds()
        observeFavorites()
    }

    private fun getBreeds() {

        viewModelScope.launch {//Entorno que maneja tareas asíncronas y las cancela automáticamente si se cierra la pantalla (evita fugas de memoria).
            _isLoading.value = true
            _errorMessage.value = null
            try {

                val response = RetrofitInstance.api.getBreeds()

                val breedNames = response.message.keys.toList()
                _breeds.value = breedNames

            } catch (e: Exception) {
                _errorMessage.value = "Error al conectar: ${e.message}"
            }finally {
                _isLoading.value = false
            }

        }

    }


    fun getImagesByBreed(breed: String) {
        viewModelScope.launch { //Entorno que maneja tareas asíncronas y las cancela automáticamente si se cierra la pantalla (evita fugas de memoria).
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = RetrofitInstance.api.getBreedImages(breed)
                _breedImages.value = response.message
            } catch (e: Exception) {
                _errorMessage.value = "Error al cargar imágenes: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleFavorite(imageUrl: String, breed: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val favorite = FavoriteDog(imageUrl, breed)
                dogDao.insertFavorite(favorite)
                Log.d("DogViewModel", "Perro guardado en favoritos: $imageUrl")
            } catch (e: Exception) {
                Log.e("DogViewModel", "Error al guardar favorito: ${e.message}")
            }
        }
    }
    private fun observeFavorites() {
        viewModelScope.launch {
            dogDao.getAllFavorites().collect { lista ->
                _favorites.value = lista
            }
        }
    }

}