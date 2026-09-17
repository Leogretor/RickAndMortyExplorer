package com.example.rickandmortyexplorer.presentation.characterdetail

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
class CharacterDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `uiState starts as loading while character is being fetched`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val character = CompletableDeferred<Character>()
            val viewModel = CharacterDetailViewModel(
                repository = FakeCharacterRepository { character.await() },
                characterId = sampleCharacter.id,
                ioDispatcher = mainDispatcherRule.testDispatcher
            )

            assertEquals(CharacterDetailUiState.Loading, viewModel.uiState.value)

            character.complete(sampleCharacter)
            advanceUntilIdle()

            assertEquals(
                CharacterDetailUiState.Success(sampleCharacter),
                viewModel.uiState.value
            )
        }

    @Test
    fun `uiState becomes success when repository returns character`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = CharacterDetailViewModel(
                repository = FakeCharacterRepository { sampleCharacter },
                characterId = sampleCharacter.id,
                ioDispatcher = mainDispatcherRule.testDispatcher
            )

            advanceUntilIdle()

            assertEquals(
                CharacterDetailUiState.Success(sampleCharacter),
                viewModel.uiState.value
            )
        }

    @Test
    fun `uiState becomes error when repository throws`() =
        runTest(mainDispatcherRule.testDispatcher) {
            val viewModel = CharacterDetailViewModel(
                repository = FakeCharacterRepository {
                    throw IllegalStateException("Boom")
                },
                characterId = sampleCharacter.id,
                ioDispatcher = mainDispatcherRule.testDispatcher
            )

            advanceUntilIdle()

            val errorState = viewModel.uiState.value
            assertTrue(errorState is CharacterDetailUiState.Error)
            assertEquals(
                UiText.StringResource(R.string.error_load_character_details),
                (errorState as CharacterDetailUiState.Error).message
            )
        }

    @Test
    fun `retry loads character after an error`() =
        runTest(mainDispatcherRule.testDispatcher) {
            var attempts = 0
            val viewModel = CharacterDetailViewModel(
                repository = FakeCharacterRepository {
                    attempts += 1
                    if (attempts == 1) {
                        throw IllegalStateException("Boom")
                    }
                    sampleCharacter
                },
                characterId = sampleCharacter.id,
                ioDispatcher = mainDispatcherRule.testDispatcher
            )
            advanceUntilIdle()

            viewModel.loadCharacter()

            assertEquals(CharacterDetailUiState.Loading, viewModel.uiState.value)
            advanceUntilIdle()
            assertEquals(
                CharacterDetailUiState.Success(sampleCharacter),
                viewModel.uiState.value
            )
            assertEquals(2, attempts)
        }

    private class FakeCharacterRepository(
        private val characterLoader: suspend (Int) -> Character
    ) : CharacterRepository {
        override suspend fun getCharacters(): List<Character> = emptyList()

        override suspend fun getCharacterById(id: Int): Character = characterLoader(id)
    }

    private companion object {
        val sampleCharacter = Character(
            id = 1,
            name = "Rick Sanchez",
            status = "Alive",
            species = "Human",
            gender = "Male",
            origin = "Earth (C-137)",
            image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
        )
    }
}
