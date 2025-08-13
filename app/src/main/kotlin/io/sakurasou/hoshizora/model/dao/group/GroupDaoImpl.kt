package io.sakurasou.hoshizora.model.dao.group

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.GroupPageVO
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.model.dao.image.Images
import io.sakurasou.hoshizora.model.dao.strategy.Strategies
import io.sakurasou.hoshizora.model.dao.user.Users
import io.sakurasou.hoshizora.model.dto.GroupInsertDTO
import io.sakurasou.hoshizora.model.dto.GroupUpdateDTO
import io.sakurasou.hoshizora.model.entity.Group
import org.jetbrains.exposed.sql.Coalesce
import org.jetbrains.exposed.sql.Query
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.count
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.innerJoin
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.leftJoin
import org.jetbrains.exposed.sql.longLiteral
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.sum
import org.jetbrains.exposed.sql.update

/**
 * @author ShiinaKin
 * 2024/9/7 14:09
 */
class GroupDaoImpl : GroupDao {
    override fun saveGroup(groupInsertDTO: GroupInsertDTO): Long {
        val entityID =
            Groups.insertAndGetId {
                it[name] = groupInsertDTO.name
                it[description] = groupInsertDTO.description
                it[strategyId] = groupInsertDTO.strategyId
                it[config] = groupInsertDTO.config
                it[isSystemReserved] = groupInsertDTO.isSystemReserved
                it[createTime] = groupInsertDTO.createTime
            }
        return entityID.value
    }

    override fun deleteGroupById(id: Long): Int = Groups.deleteWhere { Groups.id eq id }

    override fun updateGroupById(groupUpdateDTO: GroupUpdateDTO): Int =
        Groups.update({ Groups.id eq groupUpdateDTO.id }) {
            it[name] = groupUpdateDTO.name
            it[description] = groupUpdateDTO.description
            it[strategyId] = groupUpdateDTO.strategyId
            it[config] = groupUpdateDTO.config
        }

    override fun findGroupById(id: Long): Group? =
        Groups
            .selectAll()
            .where { Groups.id eq id }
            .map {
                Group(
                    id = it[Groups.id].value,
                    name = it[Groups.name],
                    description = it[Groups.description],
                    strategyId = it[Groups.strategyId],
                    config = it[Groups.config],
                    isSystemReserved = it[Groups.isSystemReserved],
                    createTime = it[Groups.createTime],
                )
            }.firstOrNull()

    override fun doesGroupUsingStrategy(strategyId: Long): Boolean =
        Groups
            .selectAll()
            .where { Groups.strategyId eq strategyId }
            .count() > 0

    override fun pagination(pageRequest: PageRequest): PageResult<GroupPageVO> {
        val query = { query: Query ->
            query
                .adjustColumnSet {
                    leftJoin(Images) { Images.groupId eq Groups.id }
                        .leftJoin(Users) { Users.groupId eq Groups.id }
                        .innerJoin(Strategies) { Strategies.id eq Groups.strategyId }
                }.adjustSelect {
                    select(
                        Groups.fields + Strategies.id + Strategies.name + Users.id.count() +
                            Images.id.count() + Coalesce(Images.size.sum(), longLiteral(0)),
                    )
                }.groupBy(
                    Groups.id,
                    Groups.name,
                    Strategies.id,
                    Strategies.name,
                )
        }
        return fetchPage(Groups, pageRequest, query) { row ->
            GroupPageVO(
                id = row[Groups.id].value,
                name = row[Groups.name],
                strategyId = row[Strategies.id].value,
                strategyName = row[Strategies.name],
                userCount = row[Users.id.count()],
                imageCount = row[Images.id.count()],
                imageSize =
                    row[Coalesce(Images.size.sum(), longLiteral(0))]
                        .let { size -> if (size != 0L) size / 1024 / 1024.0 else 0.0 },
                isSystemReserved = row[Groups.isSystemReserved],
                createTime = row[Groups.createTime],
            )
        }
    }
}
