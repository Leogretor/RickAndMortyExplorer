package com.example.rickandmortyexplorer.presentation.characterdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rickandmortyexplorer.domain.model.Character
import com.example.rickandmortyexplorer.presentation.common.CharacterAsyncImage
import com.example.rickandmortyexplorer.ui.theme.RickAndMortyExplorerTheme

@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        CharacterDetailUiState.Loading -> CharacterDetailLoading(modifier)
        is CharacterDetailUiState.Error -> CharacterDetailError(
            message = uiState.message,
            onRetry = onRetry,
            onBackClick = onBackClick,
            modifier = modifier
        )

        is CharacterDetailUiState.Success -> CharacterDetailContent(
            character = uiState.character,
            onBackClick = onBackClick,
            modifier = modifier
        )
    }
}

@Composable
private fun CharacterDetailLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun CharacterDetailError(
    message: String,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
        )
        Button(
            onClick = onRetry,
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(text = "Retry")
        }
        OutlinedButton(
            onClick = onBackClick,
            modifier = Modifier.padding(top = 12.dp)
        ) {
            Text(text = "Back to list")
        }
    }
}

@Composable
private fun CharacterDetailContent(
    character: Character,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedButton(onClick = onBackClick) {
            Text(text = "Back to list")
        }

        CharacterAsyncImage(
            imageUrl = character.image,
            contentDescription = "Portrait of ${character.name}",
            placeholderText = character.name.firstOrNull()?.uppercase() ?: "?",
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(24.dp)),
            placeholderTextStyle = MaterialTheme.typography.displayMedium,
            placeholderModifier = Modifier.padding(vertical = 72.dp)
        )

        Text(
            text = character.name,
            style = MaterialTheme.typography.headlineMedium
        )

        DetailField(label = "Species", value = character.species)
        DetailField(label = "Status", value = character.status)
        DetailField(label = "Gender", value = character.gender)
        DetailField(label = "Origin", value = character.origin)
    }
}

@Composable
private fun DetailField(label: String, value: String) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun CharacterDetailScreenPreview() {
    RickAndMortyExplorerTheme {
        CharacterDetailScreen(
            uiState = CharacterDetailUiState.Success(
                Character(
                    id = 1,
                    name = "Rick Sanchez",
                    status = "Alive",
                    species = "Human",
                    gender = "Male",
                    origin = "Earth (C-137)",
                    image = "https://rickandmortyapi.com/api/character/avatar/1.jpeg"
                )
            ),
            onRetry = {},
            onBackClick = {}
        )
    }
}
