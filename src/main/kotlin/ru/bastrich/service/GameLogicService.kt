package ru.bastrich.service

import org.springframework.stereotype.Service
import ru.bastrich.dto.Move
import ru.bastrich.dto.MovePair

@Service
class GameLogicService {

    /**
     * @return calculated competitor move
     */
    fun makeMove(moveHistory: List<MovePair>): Move {
        if (moveHistory.isEmpty()) {
            return Move.values().random()
        }

        val (lastPlayerMove, lastCompetitorMove) = moveHistory.last()
        val newCompetitorMove = when {
            lastPlayerMove == Move.ROCK && lastCompetitorMove == Move.ROCK -> Move.SCISSORS
            lastPlayerMove == Move.PAPER && lastCompetitorMove == Move.PAPER -> Move.ROCK
            lastPlayerMove == Move.SCISSORS && lastCompetitorMove == Move.SCISSORS -> Move.PAPER
            else -> throw IllegalStateException("Unreachable state, something is wrong with game implementation")
        }

        return newCompetitorMove
    }
}