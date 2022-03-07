package ru.bastrich.dto

data class FinishGameRequest(
    val userId: Long,
    val gameId: Long
)
