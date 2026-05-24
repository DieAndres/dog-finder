package com.example.dogfinder.data.api

import com.example.dogfinder.models.BreedResponse
import retrofit2.http.GET

interface DogApiService {

    @GET("breeds/list/all")
    suspend fun getBreeds(): BreedResponse
 /*función asíncrona: con suspend fun y devuelve un objeto BreedResponse*/
}