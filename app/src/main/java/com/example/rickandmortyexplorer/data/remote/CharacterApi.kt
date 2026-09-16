package com.example.rickandmortyexplorer.data.remote

import com.example.rickandmortyexplorer.data.remote.model.CharactersResponseDto
import com.example.rickandmortyexplorer.data.remote.model.CharacterDto
import retrofit2.http.GET
import retrofit2.http.Path

interface CharacterApi {

    @GET("character")
    suspend fun getAllCharacters(): CharactersResponseDto

    @GET("character/{id}")
    suspend fun getCharacterById(@Path("id") id: Int): CharacterDto
}


