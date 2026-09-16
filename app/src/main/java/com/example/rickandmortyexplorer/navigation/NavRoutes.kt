package com.example.rickandmortyexplorer.navigation

object NavRoutes {
    const val CHARACTER_LIST = "characters"
    const val CHARACTER_ID_ARG = "characterId"
    const val CHARACTER_DETAIL = "characters/{$CHARACTER_ID_ARG}"

    fun characterDetail(characterId: Int): String = "characters/$characterId"
}

