package ru.bastrich.db.dao

import mu.KotlinLogging
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.RowMapper
import org.springframework.jdbc.core.RowMapperResultSetExtractor
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.bastrich.db.entity.Game
import ru.bastrich.dto.GameFull
import ru.bastrich.dto.GameResult
import ru.bastrich.dto.GameState
import ru.bastrich.dto.Move
import ru.bastrich.dto.MovePair
import java.sql.Types

@Repository
open class GameDao(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun addGame(userId: Long): Game {
        logger.info { "Started adding game for user ${userId}" }

        val paramSource = MapSqlParameterSource(
            mapOf(
                "userId" to userId,
                "state" to GameState.IN_PROGRESS.name
            )
        )
            .apply { registerSqlType("game_state", Types.VARCHAR) }
        val keyHolder = GeneratedKeyHolder()

        jdbcTemplate.update(insertSql, paramSource, keyHolder)

        logger.info { "Finished adding game for user ${userId}, created game id - " }

        val gameId = keyHolder.key?.toLong() ?: throw IllegalStateException("Game Id can't be null here")
        return Game(gameId, userId, GameState.IN_PROGRESS)
    }

    fun getGame(gameId: Long, forUpdate: Boolean = false): Game? {
        logger.info { "Start getting game by id $gameId" }

        val result = jdbcTemplate
            .query(
                selectByIdSql.addSelectForUpdate(forUpdate),
                MapSqlParameterSource(mapOf("id" to gameId))
                    .apply { registerSqlType("game_state", Types.VARCHAR) }
                    .apply { registerSqlType("game_result", Types.VARCHAR) },
                resultSetExtractor
            )
            .orEmpty()

        logger.info { "Finished getting game by id $gameId" }

        return if (result.isEmpty()) null else result.first()
    }

    /**
     * @return number of affected rows
     */
    fun setState(gameId: Long, state: GameState): Int {
        logger.info { "Start setting state $state for game $gameId" }

        val affectedRowsNumber = jdbcTemplate
            .update(
                setStateSql,
                MapSqlParameterSource(
                    mapOf(
                        "id" to gameId,
                        "gameState" to state.name
                    )
                )
                    .apply { registerSqlType("game_state", Types.VARCHAR) }
            )

        logger.info { "Finished setting state $state for game $gameId" }

        return affectedRowsNumber
    }

    /**
     * @return number of affected rows
     */
    fun setResult(gameId: Long, result: GameResult): Int {
        logger.info { "Start setting result $result for game $gameId" }

        val affectedRowsNumber = jdbcTemplate
            .update(
                setResultSql,
                MapSqlParameterSource(
                    mapOf(
                        "id" to gameId,
                        "gameResult" to result.name
                    )
                )
                    .apply { registerSqlType("game_result", Types.VARCHAR) }
            )

        logger.info { "Finished setting result $result for game $gameId" }

        return affectedRowsNumber
    }

    fun getGames(userId: Long): List<GameFull> {
        logger.info { "Start getting all games for user id $userId" }

        val result = jdbcTemplate
            .query(
                selectGamesFullByUserIdSql,
                MapSqlParameterSource(mapOf("userId" to userId))
                    .apply { registerSqlType("game_result", Types.VARCHAR) },
                gameFullResultSetExtractor
            )
            .orEmpty()

        logger.info { "Finished getting all games for user id $userId" }

        return result
    }
}

private const val tableName = "games"
private const val movesTableName = "moves"

private const val insertSql = """
    INSERT INTO $tableName (
        user_id,
        state) 
    VALUES (
        :userId,
        :state
    ) RETURNING id
"""
private const val selectByIdSql = "SELECT id, user_id, state FROM $tableName WHERE id = :id"
private const val setStateSql = "UPDATE $tableName SET state = :gameState WHERE id = :id"
private const val setResultSql = "UPDATE $tableName SET result = :gameResult WHERE id = :id"
private const val selectGamesFullByUserIdSql = """
    SELECT 
        id,
        user_id,
        result,
        array_agg(ARRAY [player_move, competitor_move]) as moves
    FROM
        (SELECT 
            user_id, 
            id, 
            result
        FROM $tableName WHERE user_id = :userId ORDER BY id) g 
    JOIN 
        (SELECT game_id, player_move, competitor_move FROM $movesTableName ORDER BY number) m
    ON g.id = m.game_id
    GROUP BY id, user_id, result
"""

private fun String.addSelectForUpdate(forUpdate: Boolean = true) = if (forUpdate) "$this ORDER BY ID FOR NO KEY UPDATE" else this

private val logger = KotlinLogging.logger {}

private val resultSetExtractor = RowMapperResultSetExtractor(DataClassRowMapper(Game::class.java))

@Suppress("UNCHECKED_CAST")
private val gameFullResultSetExtractor = RowMapperResultSetExtractor(RowMapper { rs, rowNum ->
    GameFull(
        rs.getLong("user_id"),
        rs.getLong("id"),
        GameResult.valueOf(rs.getString("result")),
        (rs.getArray("moves")?.array as Array<Array<String>>?)
            ?.map {
                val (playerMove, competitorMove) = it.map { Move.valueOf(it) }
                MovePair(playerMove, competitorMove)
            }
            ?: emptyList()
    )
})