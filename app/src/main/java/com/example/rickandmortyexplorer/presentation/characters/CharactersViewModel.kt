package com.example.rickandmortyexplorer.presentation.characters

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyexplorer.domain.repository.CharacterRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharactersViewModel(
    private val repository: CharacterRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val coroutineScope: CoroutineScope? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharactersUiState>(CharactersUiState.Loading)
    val uiState: StateFlow<CharactersUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private val scope: CoroutineScope
        get() = coroutineScope ?: viewModelScope

    init {
        loadCharacters()
    }

    fun loadCharacters() {
        loadJob?.cancel()
        loadJob = scope.launch(ioDispatcher) {
            _uiState.value = CharactersUiState.Loading
            _uiState.value = try {
                CharactersUiState.Success(repository.getCharacters())
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                CharactersUiState.Error(
                    exception.message ?: "Failed to load characters."
                )
            }
        }
    }
}

class CharactersViewModelFactory(
    private val repository: CharacterRepository,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharactersViewModel::class.java)) {
            return CharactersViewModel(repository, ioDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

