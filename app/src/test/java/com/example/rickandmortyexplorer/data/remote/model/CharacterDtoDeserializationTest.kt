package com.example.rickandmortyexplorer.data.remote.model

import com.google.gson.Gson
import org.junit.Assert.assertEquals
import org.junit.Test

class CharacterDtoDeserializationTest {

    @Test
    fun `gson deserializes character response from api schema`() {
        val json = """
            {
              "info": {
                "count": 1,
                "pages": 1,
                "next": null,
                "prev": "https://rickandmortyapi.com/api/character?page=1"
              },
              "results": [
                {
                  "id": 1,
                  "name": "Rick Sanchez",
                  "status": "Alive",
                  "species": "Human",
                  "type": "",
                  "gender": "Male",
                  "origin": {
                    "name": "Earth (C-137)",
                    "url": "https://rickandmortyapi.com/api/location/1"
                  },
                  "location": {
                    "name": "Citadel of Ricks",
                    "url": "https://rickandmortyapi.com/api/location/3"
                  },
                  "image": "https://rickandmortyapi.com/api/character/avatar/1.jpeg",
                  "episode": [
                    "https://rickandmortyapi.com/api/episode/1",
                    "https://rickandmortyapi.com/api/episode/2"
                  ],
                  "url": "https://rickandmortyapi.com/api/character/1",
                  "created": "2017-11-04T18:48:46.250Z"
                }
              ]
            }
        """.trimIndent()

        val response = Gson().fromJson(json, CharactersResponseDto::class.java)
        val character = response.results.single()

        assertEquals(PageInfoDto(1, 1, null, "https://rickandmortyapi.com/api/character?page=1"), response.info)
        assertEquals(1, character.id)
        assertEquals("Rick Sanchez", character.name)
        assertEquals("Alive", character.status)
        assertEquals("Human", character.species)
        assertEquals("", character.type)
        assertEquals("Male", character.gender)
        assertEquals(
            NamedApiResourceDto("Earth (C-137)", "https://rickandmortyapi.com/api/location/1"),
            character.origin
        )
        assertEquals(
            NamedApiResourceDto("Citadel of Ricks", "https://rickandmortyapi.com/api/location/3"),
            character.location
        )
        assertEquals(
            listOf(
                "https://rickandmortyapi.com/api/episode/1",
                "https://rickandmortyapi.com/api/episode/2"
            ),
            character.episode
        )
        assertEquals("https://rickandmortyapi.com/api/character/avatar/1.jpeg", character.image)
        assertEquals("https://rickandmortyapi.com/api/character/1", character.url)
        assertEquals("2017-11-04T18:48:46.250Z", character.created)
    }
}
