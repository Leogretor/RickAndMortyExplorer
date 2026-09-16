package com.example.rickandmortyexplorer.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.rickandmortyexplorer.R
import com.example.rickandmortyexplorer.domain.repository.CharacterRepository
import com.example.rickandmortyexplorer.presentation.characterdetail.CharacterDetailScreen
import com.example.rickandmortyexplorer.presentation.characterdetail.CharacterDetailUiState
import com.example.rickandmortyexplorer.presentation.characterdetail.CharacterDetailViewModel
import com.example.rickandmortyexplorer.presentation.characterdetail.CharacterDetailViewModelFactory
import com.example.rickandmortyexplorer.presentation.characters.CharactersScreen
import com.example.rickandmortyexplorer.presentation.characters.CharactersViewModel
import com.example.rickandmortyexplorer.presentation.characters.CharactersViewModelFactory
import com.example.rickandmortyexplorer.presentation.common.UiText

@Composable
fun AppNavHost(
    navController: NavHostController,
    repository: CharacterRepository,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = NavRoutes.CHARACTER_LIST,
        modifier = modifier
    ) {
        composable(route = NavRoutes.CHARACTER_LIST) {
            val viewModel: CharactersViewModel = viewModel(
                factory = CharactersViewModelFactory(repository)
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                CharactersScreen(
                    uiState = uiState,
                    onRetry = viewModel::loadCharacters,
                    onCharacterClick = { characterId ->
                        navController.navigate(NavRoutes.characterDetail(characterId))
                    },
                    modifier = Modifier.padding(innerPadding)
                )
            }
        }

        composable(
            route = NavRoutes.CHARACTER_DETAIL,
            arguments = listOf(
                navArgument(NavRoutes.CHARACTER_ID_ARG) {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments
                ?.takeIf { it.containsKey(NavRoutes.CHARACTER_ID_ARG) }
                ?.getInt(NavRoutes.CHARACTER_ID_ARG)

            if (characterId == null) {
                CharacterDetailScreen(
                    uiState = CharacterDetailUiState.Error(
                        UiText.StringResource(R.string.character_open_error)
                    ),
                    onRetry = { navController.popBackStack() },
                    onBackClick = { navController.popBackStack() },
                    modifier = Modifier.fillMaxSize()
                )
                return@composable
            }

            val viewModel: CharacterDetailViewModel = viewModel(
                factory = CharacterDetailViewModelFactory(
                    repository = repository,
                    characterId = characterId
                )
            )
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            CharacterDetailScreen(
                uiState = uiState,
                onRetry = viewModel::loadCharacter,
                onBackClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
