package com.example.rickandmortyexplorer.data.remote.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CharacterDtoSerializationTest {

    @Test
    fun `character dto matches the provided schema`() {
        val character = CharacterDto(
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
            episode = (1..51).map { "https://rickandmortyapi.com/api/episode/$it" },
            url = "https://rickandmortyapi.com/api/character/1",
            created = "2017-11-04T18:48:46.250Z"
        )

        assertEquals(1, character.id)
        assertEquals("Rick Sanchez", character.name)
        assertEquals("Alive", character.status)
        assertEquals("Human", character.species)
        assertEquals("", character.type)
        assertEquals("Male", character.gender)
        assertEquals("Earth (C-137)", character.origin.name)
        assertEquals("Citadel of Ricks", character.location.name)
        assertEquals(51, character.episode.size)
        assertEquals("https://rickandmortyapi.com/api/episode/1", character.episode.first())
        assertEquals("https://rickandmortyapi.com/api/episode/51", character.episode.last())
        assertTrue(character.image.endsWith("1.jpeg"))
        assertEquals("https://rickandmortyapi.com/api/character/1", character.url)
        assertEquals("2017-11-04T18:48:46.250Z", character.created)
    }
}

