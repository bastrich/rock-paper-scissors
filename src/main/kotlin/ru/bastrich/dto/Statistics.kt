package ru.bastrich.dto

data class Statistics(
    val gameFulls: List<GameFull>
)

data class GameFull(
    val userId: Long,
    val gameId: Long,
    val gameResult: GameResult,
    val moves: List<MovePair>
)

data class MovePair(
    val playerMove: Move,
    val competitorMove: Move
)