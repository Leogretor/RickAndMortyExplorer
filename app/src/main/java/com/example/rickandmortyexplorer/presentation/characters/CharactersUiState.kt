package com.example.rickandmortyexplorer.presentation.characters

import com.example.rickandmortyexplorer.domain.model.Character

sealed interface CharactersUiState {
    data object Loading : CharactersUiState
    data class Success(val characters: List<Character>) : CharactersUiState
    data class Error(val message: String) : CharactersUiState
}

