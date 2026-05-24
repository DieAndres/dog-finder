package com.example.dogfinder.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
// Esta clase se encarga de conectar la app con la API de perros.
// Retrofit seria como un axios en js
object RetrofitInstance {

    private const val BASE_URL = "https://dog.ceo/api/"

    val api: DogApiService by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DogApiService::class.java)

    }

}