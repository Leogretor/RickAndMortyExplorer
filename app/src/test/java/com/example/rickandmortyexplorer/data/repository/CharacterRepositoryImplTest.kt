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
        val repository = CharacterRepositoryImpl(
            characterApi = FakeCharacterApi(
                CharactersResponseDto(
                    info = PageInfoDto(
                        count = 2,
                        pages = 1,
                        next = null,
                        prev = null
                    ),
                    results = listOf(
                        CharacterDto(
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
                        ),
                        CharacterDto(
                            id = 2,
                            name = "Morty Smith",
                            status = "Alive",
                            species = "Human",
                            type = "",
                            gender = "Male",
                            origin = NamedApiResourceDto(
                                name = "unknown",
                                url = ""
                            ),
                            location = NamedApiResourceDto(
                                name = "Earth (Replacement Dimension)",
                                url = "https://rickandmortyapi.com/api/location/20"
                            ),
                            image = "https://rickandmortyapi.com/api/character/avatar/2.jpeg",
                            episode = listOf("https://rickandmortyapi.com/api/episode/1"),
                            url = "https://rickandmortyapi.com/api/character/2",
                            created = "2017-11-04T18:50:21.651Z"
                        )
                    )
                )
            )
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
    }

    private class FakeCharacterApi(
        private val response: CharactersResponseDto
    ) : CharacterApi {
        override suspend fun getAllCharacters(): CharactersResponseDto = response

        override suspend fun getCharacterById(id: Int): CharacterDto {
            return response.results.first { it.id == id }
        }
    }
}

