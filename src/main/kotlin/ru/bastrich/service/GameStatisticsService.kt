package ru.bastrich.service

import org.springframework.stereotype.Service
import ru.bastrich.db.dao.GameDao
import ru.bastrich.db.dao.UserDao
import ru.bastrich.dto.Statistics
import ru.bastrich.exception.UserDoesntExistException

@Service
class GameStatisticsService(
    val userDao: UserDao,
    val gameDao: GameDao
) {

    fun getStatistics(userId: Long): Statistics {
        userDao.getUser(userId) ?: throw UserDoesntExistException(userId)
        return Statistics(gameDao.getGames(userId))
    }
}