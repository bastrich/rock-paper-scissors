package ru.bastrich.dto

data class MakeMoveRequest(
    val userId: Long,
    val gameId: Long,
    val move: Move
)
