package ru.bastrich.db.dao

import mu.KotlinLogging
import org.springframework.jdbc.core.DataClassRowMapper
import org.springframework.jdbc.core.RowMapperResultSetExtractor
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.jdbc.support.GeneratedKeyHolder
import org.springframework.stereotype.Repository
import ru.bastrich.db.entity.User

@Repository
open class UserDao(private val jdbcTemplate: NamedParameterJdbcTemplate) {

    fun addUser(): User {
        logger.info { "Started adding user" }

        val keyHolder = GeneratedKeyHolder()
        jdbcTemplate.update(insertSql, MapSqlParameterSource(), keyHolder)

        logger.info { "Finished adding user" }

        val userId = keyHolder.key?.toLong() ?: throw IllegalStateException("User Id can't be null here")
        return User(userId)
    }

    fun getUser(userId: Long): User? {
        logger.info { "Start getting user by id $userId" }

        val result = jdbcTemplate
            .query(
                selectByIdSql,
                MapSqlParameterSource(mapOf("id" to userId)),
                resultSetExtractor
            )
            .orEmpty()

        logger.info { "Finished getting user by id $userId" }

        return if (result.isEmpty()) null else result.first()
    }
}

private const val tableName = "users"

private const val insertSql = "INSERT INTO $tableName DEFAULT VALUES RETURNING id"
private const val selectByIdSql = "SELECT id FROM $tableName WHERE id = :id"

private val resultSetExtractor = RowMapperResultSetExtractor(DataClassRowMapper(User::class.java))

private val logger = KotlinLogging.logger {}