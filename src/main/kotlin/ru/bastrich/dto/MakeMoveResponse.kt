package ru.bastrich.dto

data class MakeMoveResponse(
    val competitorMove: Move,
    val gameState: GameState
)