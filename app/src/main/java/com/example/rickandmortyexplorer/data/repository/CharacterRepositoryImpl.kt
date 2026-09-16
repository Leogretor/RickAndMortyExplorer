package com.example.rickandmortyexplorer.data.repository

import com.example.rickandmortyexplorer.data.mapper.toCharacter
import com.example.rickandmortyexplorer.data.remote.CharacterApi
import com.example.rickandmortyexplorer.domain.model.Character
import com.example.rickandmortyexplorer.domain.repository.CharacterRepository

class CharacterRepositoryImpl(
    private val characterApi: CharacterApi
) : CharacterRepository {

    override suspend fun getCharacters(): List<Character> {
        return characterApi.getAllCharacters().results.map { it.toCharacter() }
    }

    override suspend fun getCharacterById(id: Int): Character {
        return characterApi.getCharacterById(id).toCharacter()
    }
}

