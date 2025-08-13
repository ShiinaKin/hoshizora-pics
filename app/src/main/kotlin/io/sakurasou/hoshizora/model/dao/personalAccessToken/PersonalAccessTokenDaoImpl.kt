package io.sakurasou.hoshizora.model.dao.personalAccessToken

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.PersonalAccessTokenPageVO
import io.sakurasou.hoshizora.model.dao.personalAccessToken.PersonalAccessTokens.expireTime
import io.sakurasou.hoshizora.model.dto.PersonalAccessTokenInsertDTO
import io.sakurasou.hoshizora.model.dto.PersonalAccessTokenUpdateDTO
import io.sakurasou.hoshizora.model.entity.PersonalAccessToken
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.andWhere
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

/**
 * @author ShiinaKin
 * 2024/11/14 22:13
 */
class PersonalAccessTokenDaoImpl : PersonalAccessTokenDao {
    override fun savePAT(insertDTO: PersonalAccessTokenInsertDTO): Long {
        val entityId =
            PersonalAccessTokens.insertAndGetId {
                it[userId] = insertDTO.userId
                it[name] = insertDTO.name
                it[description] = insertDTO.description
                it[createTime] = insertDTO.createTime
                it[expireTime] = insertDTO.expireTime
            }
        return entityId.value
    }

    override fun deletePATById(patId: Long): Int = PersonalAccessTokens.deleteWhere { id eq patId }

    override fun updatePATById(updateDTO: PersonalAccessTokenUpdateDTO): Int =
        PersonalAccessTokens.update({ PersonalAccessTokens.id eq updateDTO.id }) {
            it[name] = updateDTO.name
            it[description] = updateDTO.description
        }

    override fun findPATById(patId: Long): PersonalAccessToken? =
        PersonalAccessTokens
            .selectAll()
            .where { PersonalAccessTokens.id eq patId }
            .map(::toPersonalAccessToken)
            .firstOrNull()

    override fun findPATByUserId(userId: Long): List<PersonalAccessToken> =
        PersonalAccessTokens
            .selectAll()
            .where { PersonalAccessTokens.userId eq userId }
            .map(::toPersonalAccessToken)

    override fun pagination(
        userId: Long,
        pageRequest: PageRequest,
    ): PageResult<PersonalAccessTokenPageVO> {
        val query = { query: Query ->
            query
                .adjustWhere { PersonalAccessTokens.userId eq userId }
                .also {
                    pageRequest.additionalCondition?.let { condition ->
                        condition["isExpired"]?.let { isExpired ->
                            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
                            query.andWhere {
                                if (isExpired == "true") {
                                    expireTime lessEq now
                                } else {
                                    expireTime greater now
                                }
                            }
                        }
                    }
                }
        }
        return fetchPage(PersonalAccessTokens, pageRequest, query) { resultRow ->
            val now = Clock.System.now().toLocalDateTime(TimeZone.UTC)
            PersonalAccessTokenPageVO(
                id = resultRow[PersonalAccessTokens.id].value,
                name = resultRow[PersonalAccessTokens.name],
                description = resultRow[PersonalAccessTokens.description],
                createTime = resultRow[PersonalAccessTokens.createTime],
                expireTime = resultRow[expireTime],
                isExpired = resultRow[expireTime] <= now,
            )
        }
    }

    private fun toPersonalAccessToken(resultRow: ResultRow) =
        PersonalAccessToken(
            resultRow[PersonalAccessTokens.id].value,
            resultRow[PersonalAccessTokens.userId],
            resultRow[PersonalAccessTokens.name],
            resultRow[PersonalAccessTokens.description],
            resultRow[PersonalAccessTokens.createTime],
            resultRow[expireTime],
        )
}
