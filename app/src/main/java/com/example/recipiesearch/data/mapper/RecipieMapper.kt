package com.example.recipiesearch.data.mapper

import com.example.recipiesearch.data.local.RecipieEntity
import com.example.recipiesearch.data.remote.dto.RecipieDto
import com.example.recipiesearch.domain.model.Recipie

fun RecipieDto.toRecipieEntity(): RecipieEntity {
    return RecipieEntity(
        id = id,
        image = image,
        title = title,
        readyInMinutes = readyInMinutes
    )
}

fun RecipieEntity.toRecipieDto(): RecipieDto {
    return RecipieDto(
        id = id,
        image = image,
        title = title,
        readyInMinutes = readyInMinutes
    )
}


fun RecipieDto.toRecipie(): Recipie {
    return Recipie(
        id = id,
        image = image,
        title = title,
        readyInMinutes = readyInMinutes
    )
}

fun RecipieEntity.toRecipie(): Recipie {
    return Recipie(
        id = id,
        image = image,
        title = title,
        readyInMinutes = readyInMinutes
    )
}

fun Recipie.toRecipieEntity(): RecipieEntity {
    return RecipieEntity(
        id = id,
        image = image,
        title = title,
        readyInMinutes = readyInMinutes
    )
}
