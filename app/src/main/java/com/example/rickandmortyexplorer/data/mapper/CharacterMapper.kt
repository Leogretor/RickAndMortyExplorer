package com.example.rickandmortyexplorer.data.mapper

import com.example.rickandmortyexplorer.data.remote.model.CharacterDto
import com.example.rickandmortyexplorer.domain.model.Character

fun CharacterDto.toCharacter(): Character = Character(
    id = id,
    name = name,
    status = status,
    species = species,
    gender = gender,
    origin = origin.name,
    image = image
)

