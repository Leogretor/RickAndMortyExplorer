package com.example.rickandmortyexplorer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.rickandmortyexplorer.data.remote.RickAndMortyApi
import com.example.rickandmortyexplorer.data.repository.CharacterRepositoryImpl
import com.example.rickandmortyexplorer.navigation.AppNavHost
import com.example.rickandmortyexplorer.ui.theme.RickAndMortyExplorerTheme

class MainActivity : ComponentActivity() {

    private val repository by lazy(LazyThreadSafetyMode.NONE) {
        CharacterRepositoryImpl(RickAndMortyApi.service)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            RickAndMortyExplorerTheme {
                AppNavHost(
                    navController = navController,
                    repository = repository,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
