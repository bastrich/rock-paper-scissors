package ru.bastrich.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import ru.bastrich.db.dao.GameDao
import ru.bastrich.db.dao.MoveDao
import ru.bastrich.db.dao.UserDao
import ru.bastrich.dto.GameResult
import ru.bastrich.dto.GameState
import ru.bastrich.dto.MakeMoveResponse
import ru.bastrich.dto.Move
import ru.bastrich.exception.GameDoesntExistException
import ru.bastrich.exception.IllegalMoveException
import ru.bastrich.exception.UserDoesntExistException

@Service
open class GameService(
    private val gameDao: GameDao,
    private val userDao: UserDao,
    private val moveDao: MoveDao,
    private val gameLogicService: GameLogicService
) {

    fun startGame(userId: Long): Long {
        userDao.getUser(userId) ?: throw UserDoesntExistException(userId)
        return gameDao.addGame(userId).id
    }

    @Transactional
    open fun makeMove(userId: Long, gameId: Long, playerMove: Move): MakeMoveResponse {
        val user = userDao.getUser(userId) ?: throw UserDoesntExistException(userId)
        val game = gameDao.getGame(gameId, true) ?: throw GameDoesntExistException(gameId)

        if (user.id != game.userId) {
            throw IllegalMoveException(userId, gameId)
        }
        if (game.state != GameState.IN_PROGRESS) {
            throw IllegalStateException("Can't make moves when the game is already finished with the state ${game.state}")
        }

        val moveHistory = moveDao.getMoves(gameId)

        val competitorMove = gameLogicService.makeMove(moveHistory)
        val gameState = calculateGameState(playerMove, competitorMove)

        moveDao.addMove(gameId, playerMove, competitorMove, moveHistory.size + 1)
        gameDao.setState(gameId, gameState)

        return MakeMoveResponse(competitorMove, gameState)
    }

    @Transactional
    open fun finishGame(userId: Long, gameId: Long): GameResult {
        val user = userDao.getUser(userId) ?: throw UserDoesntExistException(userId)
        val game = gameDao.getGame(gameId, true) ?: throw GameDoesntExistException(gameId)

        if (user.id != game.userId) {
            throw IllegalMoveException(userId, gameId)
        }

        val gameResult = when (game.state) {
            GameState.IN_PROGRESS -> GameResult.INTERRUPTED_BY_PLAYER
            GameState.PLAYER_WIN -> GameResult.PLAYER_WIN
            GameState.COMPETITOR_WIN -> GameResult.COMPETITOR_WIN
        }

        gameDao.setResult(gameId, gameResult)
        return gameResult
    }

    private fun calculateGameState(playerMove: Move, competitorMove: Move) = when {
        playerMove == Move.ROCK && competitorMove == Move.ROCK -> GameState.IN_PROGRESS
        playerMove == Move.ROCK && competitorMove == Move.PAPER -> GameState.COMPETITOR_WIN
        playerMove == Move.ROCK && competitorMove == Move.SCISSORS -> GameState.PLAYER_WIN
        playerMove == Move.PAPER && competitorMove == Move.ROCK -> GameState.PLAYER_WIN
        playerMove == Move.PAPER && competitorMove == Move.PAPER -> GameState.IN_PROGRESS
        playerMove == Move.PAPER && competitorMove == Move.SCISSORS -> GameState.COMPETITOR_WIN
        playerMove == Move.SCISSORS && competitorMove == Move.ROCK -> GameState.COMPETITOR_WIN
        playerMove == Move.SCISSORS && competitorMove == Move.PAPER -> GameState.PLAYER_WIN
        playerMove == Move.SCISSORS && competitorMove == Move.SCISSORS -> GameState.IN_PROGRESS
        else -> throw IllegalStateException("Unreachable state, something is wrong with game implementation")
    }
}