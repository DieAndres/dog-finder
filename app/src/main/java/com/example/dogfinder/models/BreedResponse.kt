package com.example.dogfinder.models

data class BreedResponse(
    val message: Map<String, List<String>>,
    val status: String
)