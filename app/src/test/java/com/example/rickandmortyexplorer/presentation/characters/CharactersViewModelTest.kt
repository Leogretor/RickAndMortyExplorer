package com.example.rickandmortyexplorer.presentation.characters

import com.example.rickandmortyexplorer.R
import com.example.rickandmortyexplorer.domain.model.Character
import com.example.rickandmortyexplorer.domain.repository.CharacterRepository
import com.example.rickandmortyexplorer.presentation.common.MainDispatcherRule
import com.example.rickandmortyexplorer.presentation.common.UiText
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class CharactersViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `uiState starts as loading while characters are being fetched`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val characters = CompletableDeferred<List<Character>>()
            val viewModel = CharactersViewModel(
                repository = FakeCharacterRepository { characters.await() },
                ioDispatcher = mainDispatcherRule.testDispatcher
            )

            assertEquals(CharactersUiState.Loading, viewModel.uiState.value)

            characters.complete(sampleCharacters)
            advanceUntilIdle()

            assertEquals(CharactersUiState.Success(sampleCharacters), viewModel.uiState.value)
        }

    @Test
    fun `uiState becomes success when repository returns characters`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = CharactersViewModel(
                repository = FakeCharacterRepository { sampleCharacters },
                ioDispatcher = mainDispatcherRule.testDispatcher
            )

            advanceUntilIdle()

            assertEquals(
                CharactersUiState.Success(sampleCharacters),
                viewModel.uiState.value
            )
        }

    @Test
    fun `uiState becomes error when repository throws`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = CharactersViewModel(
                repository = FakeCharacterRepository { throw IllegalStateException("Boom") },
                ioDispatcher = mainDispatcherRule.testDispatcher
            )

            advanceUntilIdle()

            val errorState = viewModel.uiState.value
            assertTrue(errorState is CharactersUiState.Error)
            assertEquals(
                UiText.StringResource(R.string.error_load_characters),
                (errorState as CharactersUiState.Error).message
            )
        }

    private class FakeCharacterRepository(
        private val loader: suspend () -> List<Character>
    ) : CharacterRepository {
        override suspend fun getCharacters(): List<Character> = loader()

        override suspend fun getCharacterById(id: Int): Character {
            return loader().first { it.id == id }
        }
    }

    private companion object {
        val sampleCharacters = listOf(
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
        )
    }
}
