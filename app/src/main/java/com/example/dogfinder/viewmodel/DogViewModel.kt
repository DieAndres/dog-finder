package com.example.dogfinder.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dogfinder.data.api.RetrofitInstance
import kotlinx.coroutines.launch
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
/*HOOK*/
class DogViewModel : ViewModel() {

    private val _breeds = mutableStateOf<List<String>>(emptyList())
    val breeds: State<List<String>> = _breeds
    private val _isLoading = mutableStateOf(true)
    val isLoading: State<Boolean> = _isLoading
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage
    //ejecutar automáticamente cuando se crea el ViewModel
    init {
        getBreeds()
    }

    private fun getBreeds() {

        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {

                val response = RetrofitInstance.api.getBreeds()

                val breedNames = response.message.keys.toList() // Obtenemos solo los nombres (claves del mapa)
                _breeds.value = breedNames

            } catch (e: Exception) {
                _errorMessage.value = "Error al conectar: ${e.message}"
            }finally {
                _isLoading.value = false
            }

        }

    }

}