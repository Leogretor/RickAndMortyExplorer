package com.example.rickandmortyexplorer.data.repository

import com.example.rickandmortyexplorer.data.remote.CharacterApi
import com.example.rickandmortyexplorer.data.remote.model.CharacterDto
import com.example.rickandmortyexplorer.data.remote.model.CharactersResponseDto
import com.example.rickandmortyexplorer.data.remote.model.NamedApiResourceDto
import com.example.rickandmortyexplorer.data.remote.model.PageInfoDto
import com.example.rickandmortyexplorer.domain.model.Character
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterRepositoryImplTest {

    @Test
    fun `getCharacters fetches characters from CharacterApi and maps them to domain models`() = runBlocking {
        val characterApi = FakeCharacterApi(
            CharactersResponseDto(
                info = PageInfoDto(
                    count = 2,
                    pages = 1,
                    next = null,
                    prev = null
                ),
                results = listOf(rickDto, mortyDto)
            )
        )
        val repository = CharacterRepositoryImpl(
            characterApi = characterApi
        )

        val characters = repository.getCharacters()

        assertEquals(
            listOf(
                Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    species = "Human",
                    gender = "Male",
                    origin = "Earth (C-137)",
                    image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
                ),
                Character(
                    id = 2,
                    name = "Morty Smith",
                    status = "Alive",
                    species = "Human",
                    gender = "Male",
                    origin = "unknown",
                    image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
                )
            ),
            characters
        )
        assertEquals(1, characterApi.getAllCharactersCalls)
        assertEquals(emptyList<Int>(), characterApi.requestedCharacterIds)
    }

    @Test
    fun `getCharacterById forwards id and maps returned character`() = runBlocking {
        val characterApi = FakeCharacterApi(
            CharactersResponseDto(
                info = PageInfoDto(2, 1, null, null),
                results = listOf(rickDto, mortyDto)
            )
        )
        val repository = CharacterRepositoryImpl(characterApi)

        val character = repository.getCharacterById(2)

        assertEquals(
            Character(
                id = 2,
                name = "Morty Smith",
                status = "Alive",
                species = "Human",
                gender = "Male",
                origin = "unknown",
                image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg"
            ),
            character
        )
        assertEquals(listOf(2), characterApi.requestedCharacterIds)
        assertEquals(0, characterApi.getAllCharactersCalls)
    }

    private class FakeCharacterApi(
        private val response: CharactersResponseDto
    ) : CharacterApi {
        var getAllCharactersCalls = 0
            private set
        val requestedCharacterIds = mutableListOf<Int>()

        override suspend fun getAllCharacters(): CharactersResponseDto {
            getAllCharactersCalls += 1
            return response
        }

        override suspend fun getCharacterById(id: Int): CharacterDto {
            requestedCharacterIds += id
            return response.results.first { it.id == id }
        }
    }

    private companion object {
        val rickDto = CharacterDto(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = NamedApiResourceDto(
                name = "Earth (C-137)",
                url = "https://rickandmortyapi.com/api/location/1"
            ),
            location = NamedApiResourceDto(
                name = "Citadel of Ricks",
                url = "https://rickandmortyapi.com/api/location/3"
            ),
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
            episode = listOf("https://rickandmortyapi.com/api/episode/1"),
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z"
        )
        val mortyDto = CharacterDto(
            id = 2,
            name = "Morty Smith",
            status = "Alive",
            species = "Human",
            type = "",
            gender = "Male",
            origin = NamedApiResourceDto(name = "unknown", url = ""),
            location = NamedApiResourceDto(
                name = "Earth (Replacement Dimension)",
                url = "https://rickandmortyapi.com/api/location/20"
            ),
            image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
            episode = listOf("https://rickandmortyapi.com/api/episode/1"),
            url = "https://rickandmortyapi.com/api/character/2",
            created = "2017-11-04T18:50:21.651Z"
        )
    }
}
