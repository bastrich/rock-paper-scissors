package ru.bastrich.controllers

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import ru.bastrich.dto.FinishGameRequest
import ru.bastrich.dto.FinishGameResponse
import ru.bastrich.dto.MakeMoveRequest
import ru.bastrich.dto.StartGameRequest
import ru.bastrich.dto.StartGameResponse
import ru.bastrich.service.GameService
import ru.bastrich.service.GameStatisticsService

@RestController
class GameController(
    private val gameService: GameService,
    private val gameStatisticsService: GameStatisticsService
) {

    @PostMapping("/game/start")
    fun startGame(@RequestBody startGameRequest: StartGameRequest): StartGameResponse = StartGameResponse(
        gameService.startGame(startGameRequest.userId)
    )

    @PostMapping("/game/makeMove")
    fun makeMove(@RequestBody makeMoveRequest: MakeMoveRequest) = with(makeMoveRequest) {
        gameService.makeMove(userId, gameId, move)
    }

    @PostMapping("/game/finish")
    fun finishGame(@RequestBody finishGameRequest: FinishGameRequest) = FinishGameResponse(
        gameService.finishGame(finishGameRequest.userId, finishGameRequest.gameId)
    )

    @GetMapping("/statistics/{userId}")
    fun getStatistics(@PathVariable userId: Long) = gameStatisticsService.getStatistics(userId)
}

