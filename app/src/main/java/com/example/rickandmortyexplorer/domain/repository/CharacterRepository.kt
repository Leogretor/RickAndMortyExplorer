package com.example.rickandmortyexplorer.domain.repository

import com.example.rickandmortyexplorer.domain.model.Character

interface CharacterRepository {
    suspend fun getCharacters(): List<Character>
    suspend fun getCharacterById(id: Int): Character
}

