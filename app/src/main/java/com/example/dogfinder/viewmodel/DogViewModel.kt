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
    //ejecutar automáticamente cuando se crea el ViewModel
    init {
        getBreeds()
    }

    private fun getBreeds() {

        viewModelScope.launch {

            try {

                val response = RetrofitInstance.api.getBreeds()

                val breedNames = response.message.keys.toList() // Obtenemos solo los nombres (claves del mapa)
                _breeds.value = breedNames

            } catch (e: Exception) {

                Log.e("API_ERROR", e.message.toString())

            }

        }

    }

}