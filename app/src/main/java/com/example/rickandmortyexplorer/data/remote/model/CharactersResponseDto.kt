package com.example.rickandmortyexplorer.data.remote.model

data class CharactersResponseDto(
    val info: PageInfoDto,
    val results: List<CharacterDto>
)

data class PageInfoDto(
    val count: Int,
    val pages: Int,
    val next: String?,
    val prev: String?
)

data class CharacterDto(
    val id: Int,
    val name: String,
    val status: String,
    val species: String,
    val type: String,
    val gender: String,
    val origin: NamedApiResourceDto,
    val location: NamedApiResourceDto,
    val image: String,
    val episode: List<String>,
    val url: String,
    val created: String
)

data class NamedApiResourceDto(
    val name: String,
    val url: String
)

