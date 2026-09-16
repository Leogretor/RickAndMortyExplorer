package com.example.rickandmortyexplorer.presentation.characterdetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.rickandmortyexplorer.R
import com.example.rickandmortyexplorer.domain.repository.CharacterRepository
import com.example.rickandmortyexplorer.presentation.common.UiText
import com.example.rickandmortyexplorer.presentation.common.toUserMessage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CharacterDetailViewModel(
    private val repository: CharacterRepository,
    private val characterId: Int,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
    private val coroutineScope: CoroutineScope? = null
) : ViewModel() {

    private val _uiState = MutableStateFlow<CharacterDetailUiState>(CharacterDetailUiState.Loading)
    val uiState: StateFlow<CharacterDetailUiState> = _uiState.asStateFlow()

    private var loadJob: Job? = null
    private val scope: CoroutineScope
        get() = coroutineScope ?: viewModelScope

    init {
        loadCharacter()
    }

    fun loadCharacter() {
        loadJob?.cancel()
        _uiState.value = CharacterDetailUiState.Loading
        loadJob = scope.launch(ioDispatcher) {
            _uiState.value = try {
                CharacterDetailUiState.Success(repository.getCharacterById(characterId))
            } catch (cancellationException: CancellationException) {
                throw cancellationException
            } catch (exception: Exception) {
                CharacterDetailUiState.Error(
                    exception.toUserMessage(UiText.StringResource(R.string.error_load_character_details))
                )
            }
        }
    }
}

class CharacterDetailViewModelFactory(
    private val repository: CharacterRepository,
    private val characterId: Int,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CharacterDetailViewModel::class.java)) {
            return CharacterDetailViewModel(repository, characterId, ioDispatcher) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}
