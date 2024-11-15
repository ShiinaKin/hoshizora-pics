package io.sakurasou.model.dao.personalAccessToken

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.PersonalAccessTokenPageVO
import io.sakurasou.model.dto.PersonalAccessTokenInsertDTO
import io.sakurasou.model.dto.PersonalAccessTokenUpdateDTO
import io.sakurasou.model.entity.PersonalAccessToken
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * @author ShiinaKin
 * 2024/11/14 22:13
 */
class PersonalAccessTokenDaoImpl : PersonalAccessTokenDao {
    override fun savePAT(insertDTO: PersonalAccessTokenInsertDTO): Long {
        val entityId = PersonalAccessTokens.insertAndGetId {
            it[userId] = insertDTO.userId
            it[name] = insertDTO.name
            it[description] = insertDTO.description
            it[createTime] = insertDTO.createTime
            it[expireTime] = insertDTO.expireTime
        }
        return entityId.value
    }

    override fun deletePATById(patId: Long): Int {
        return PersonalAccessTokens.deleteWhere { id eq patId }
    }

    override fun updatePATById(updateDTO: PersonalAccessTokenUpdateDTO): Int {
        return PersonalAccessTokens.update({ PersonalAccessTokens.id eq updateDTO.id }) {
            it[name] = updateDTO.name
            it[description] = updateDTO.description
        }
    }

    override fun findPATById(patId: Long): PersonalAccessToken? {
        return PersonalAccessTokens.selectAll()
            .where { PersonalAccessTokens.id eq patId }
            .map(::toPersonalAccessToken)
            .firstOrNull()
    }

    override fun findPATByUserId(userId: Long): List<PersonalAccessToken> {
        return PersonalAccessTokens.selectAll()
            .where { PersonalAccessTokens.userId eq userId }
            .map(::toPersonalAccessToken)
    }

    override fun pagination(userId: Long, pageRequest: PageRequest): PageResult<PersonalAccessTokenPageVO> {
        val query = { query: Query ->
            query.adjustWhere { PersonalAccessTokens.userId eq userId }
                .also {
                    pageRequest.additionalCondition?.let { condition ->
                        condition["isExpired"]?.let { isExpired ->
                            val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
                            query.andWhere {
                                if (isExpired == "true") PersonalAccessTokens.expireTime lessEq now
                                else PersonalAccessTokens.expireTime greater now
                            }
                        }
                    }
                }
        }
        return fetchPage(PersonalAccessTokens, pageRequest, query) { resultRow ->
            PersonalAccessTokenPageVO(
                id = resultRow[PersonalAccessTokens.id].value,
                name = resultRow[PersonalAccessTokens.name],
                description = resultRow[PersonalAccessTokens.description],
                createTime = resultRow[PersonalAccessTokens.createTime],
                expireTime = resultRow[PersonalAccessTokens.expireTime]
            )
        }
    }

    private fun toPersonalAccessToken(resultRow: ResultRow) = PersonalAccessToken(
        resultRow[PersonalAccessTokens.id].value,
        resultRow[PersonalAccessTokens.userId],
        resultRow[PersonalAccessTokens.name],
        resultRow[PersonalAccessTokens.description],
        resultRow[PersonalAccessTokens.createTime],
        resultRow[PersonalAccessTokens.expireTime]
    )
}