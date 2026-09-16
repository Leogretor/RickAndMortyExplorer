package com.example.rickandmortyexplorer.presentation.characters
import com.example.rickandmortyexplorer.domain.model.Character
import com.example.rickandmortyexplorer.domain.repository.CharacterRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
class CharactersViewModelTest {
    @Test
    fun `uiState starts as loading while characters are being fetched`() {
        TestCoroutineEnvironment().use { environment ->
            val gate = CompletableDeferred<List<Character>>()
            val viewModel = CharactersViewModel(
                repository = FakeCharacterRepository { gate.await() },
                ioDispatcher = environment.dispatcher,
                coroutineScope = environment.scope
            )
            assertEquals(CharactersUiState.Loading, viewModel.uiState.value)
            gate.complete(sampleCharacters)
            awaitState(viewModel) { it is CharactersUiState.Success }
        }
    }
    @Test
    fun `uiState becomes success when repository returns characters`() {
        TestCoroutineEnvironment().use { environment ->
            val viewModel = CharactersViewModel(
                repository = FakeCharacterRepository { sampleCharacters },
                ioDispatcher = environment.dispatcher,
                coroutineScope = environment.scope
            )
            awaitState(viewModel) { it is CharactersUiState.Success }
            assertEquals(
                CharactersUiState.Success(sampleCharacters),
                viewModel.uiState.value
            )
        }
    }
    @Test
    fun `uiState becomes error when repository throws`() {
        TestCoroutineEnvironment().use { environment ->
            val viewModel = CharactersViewModel(
                repository = FakeCharacterRepository { throw IllegalStateException("Boom") },
                ioDispatcher = environment.dispatcher,
                coroutineScope = environment.scope
            )
            awaitState(viewModel) { it is CharactersUiState.Error }
            val errorState = viewModel.uiState.value
            assertTrue(errorState is CharactersUiState.Error)
            assertEquals(
                "Unable to load characters right now.",
                (errorState as CharactersUiState.Error).message
            )
        }
    }
    private fun awaitState(
        viewModel: CharactersViewModel,
        predicate: (CharactersUiState) -> Boolean
    ) = runBlocking {
        withTimeout(1.seconds) {
            while (!predicate(viewModel.uiState.value)) {
                delay(10.milliseconds)
            }
        }
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
