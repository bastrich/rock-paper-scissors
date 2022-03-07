package ru.bastrich.db.dao

import mu.KotlinLogging
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.RowMapperResultSetExtractor
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.stereotype.Repository
import ru.bastrich.dto.Move
import ru.bastrich.dto.MovePair
import java.sql.Types

@Repository
open class MoveDao(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun addMove(gameId: Long, playerMove: Move, competitorMove: Move, number: Int) {
        logger.info { "Started adding move (player=$playerMove, competitor=$competitorMove) for game ${gameId}" }

        val paramSource = MapSqlParameterSource(
            mapOf(
                "gameId" to gameId,
                "playerMove" to playerMove.name,
                "competitorMove" to competitorMove.name,
                "number" to number
            )
        )
            .apply { registerSqlType("move", Types.VARCHAR) }

        jdbcTemplate.update(insertSql, paramSource)

        logger.info { "Finished adding move (player=$playerMove, competitor=$competitorMove) for game ${gameId}" }
    }

    fun getMoves(gameId: Long, forUpdate: Boolean = false): List<MovePair> {
        logger.info { "Start getting all moves for game $gameId" }

        val result = jdbcTemplate
            .query(
                selectByGameIdSql.addSelectForUpdate(forUpdate),
                MapSqlParameterSource(mapOf("gameId" to gameId))
                    .apply { registerSqlType("move", Types.VARCHAR) },
                resultSetExtractor
            )
            .orEmpty()

        logger.info { "Finished getting all moves for game $gameId" }

        return result
    }
}

private const val tableName = "moves"

private const val insertSql = """
    INSERT INTO $tableName (
        game_id,
        player_move,
        competitor_move,
        number
    ) 
    VALUES (
        :gameId,
        :playerMove,
        :competitorMove,
        :number
    )
"""
private const val selectByGameIdSql = "SELECT player_move, competitor_move, number FROM $tableName WHERE game_id = :gameId ORDER BY number"

private fun String.addSelectForUpdate(forUpdate: Boolean = true) = if (forUpdate) "$this ORDER BY ID FOR NO KEY UPDATE" else this

private val resultSetExtractor = RowMapperResultSetExtractor(DataClassRowMapper(MovePair::class.java))

private val logger = KotlinLogging.logger {}