package com.example.rickandmortyexplorer.data.remote

import com.example.rickandmortyexplorer.data.remote.model.CharactersResponseDto
import retrofit2.http.GET

interface RickAndMortyApiService {

    @GET("character")
    suspend fun getAllCharacters(): CharactersResponseDto
}

