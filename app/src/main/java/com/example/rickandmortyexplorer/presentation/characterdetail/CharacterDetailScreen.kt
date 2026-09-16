package com.example.rickandmortyexplorer.presentation.characterdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rickandmortyexplorer.R
import com.example.rickandmortyexplorer.domain.model.Character
import com.example.rickandmortyexplorer.presentation.common.CharacterAsyncImage
import com.example.rickandmortyexplorer.presentation.common.ErrorStateContent
import com.example.rickandmortyexplorer.presentation.common.LoadingStateContent
import com.example.rickandmortyexplorer.presentation.common.UiText
import com.example.rickandmortyexplorer.ui.theme.RickAndMortyExplorerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CharacterDetailScreen(
    uiState: CharacterDetailUiState,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.character_details_title)) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.navigate_up)
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        when (uiState) {
            CharacterDetailUiState.Loading -> LoadingStateContent(
                modifier = Modifier.padding(innerPadding)
            )

            is CharacterDetailUiState.Error -> ErrorStateContent(
                message = uiState.message,
                primaryActionText = UiText.StringResource(R.string.retry),
                onPrimaryAction = onRetry,
                modifier = Modifier.padding(innerPadding)
            )

            is CharacterDetailUiState.Success -> CharacterDetailContent(
                character = uiState.character,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}

@Composable
private fun CharacterDetailContent(
    character: Character,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
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

        DetailField(label = stringResource(R.string.character_species_label), value = character.species)
        DetailField(label = stringResource(R.string.character_status_label), value = character.status)
        DetailField(label = stringResource(R.string.character_gender_label), value = character.gender)
        DetailField(label = stringResource(R.string.character_origin_label), value = character.origin)
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
