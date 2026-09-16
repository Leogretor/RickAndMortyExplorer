package com.example.rickandmortyexplorer.presentation.characterdetail

import com.example.rickandmortyexplorer.domain.model.Character

sealed interface CharacterDetailUiState {
    data object Loading : CharacterDetailUiState
    data class Success(val character: Character) : CharacterDetailUiState
    data class Error(val message: String) : CharacterDetailUiState
}

