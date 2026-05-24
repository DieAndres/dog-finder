package com.example.dogfinder.data.api

import retrofit2.http.Path
import com.example.dogfinder.models.BreedResponse
import com.example.dogfinder.models.DogImageResponse
import retrofit2.http.GET

interface DogApiService {

    @GET("breeds/list/all")
    suspend fun getBreeds(): BreedResponse
 /*función asíncrona: con suspend fun y devuelve un objeto BreedResponse*/

    @GET("breed/{breed}/images")
    suspend fun getBreedImages(@Path("breed") breed: String): DogImageResponse
    /*uso el path aca para ir colocando dinamicamente las razas*/
}