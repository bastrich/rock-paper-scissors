package ru.bastrich.db.entity

import ru.bastrich.dto.GameState

data class Game(
    val id: Long,
    val userId: Long,
    val state: GameState
)
