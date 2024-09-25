package io.sakurasou.model.dao.group

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.model.dto.GroupInsertDTO
import io.sakurasou.model.dto.GroupUpdateDTO
import io.sakurasou.model.entity.Group
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update

/**
 * @author ShiinaKin
 * 2024/9/7 14:09
 */
class GroupDaoImpl : GroupDao {
    override fun saveGroup(groupInsertDTO: GroupInsertDTO): Long {
        val entityID = Groups.insertAndGetId {
            it[name] = groupInsertDTO.name
            it[description] = groupInsertDTO.description
            it[strategyId] = groupInsertDTO.strategyId
            it[maxSize] = groupInsertDTO.maxSize
        }
        return entityID.value
    }

    override fun deleteGroupById(id: Long): Int {
        return Groups.deleteWhere { Groups.id eq id }
    }

    override fun updateGroupById(groupUpdateDTO: GroupUpdateDTO): Int {
        return Groups.update({ Groups.id eq groupUpdateDTO.id }) {
            it[name] = groupUpdateDTO.name
            it[description] = groupUpdateDTO.description
            it[strategyId] = groupUpdateDTO.strategyId
            it[maxSize] = groupUpdateDTO.maxSize
        }
    }

    override fun findGroupById(id: Long): Group? {
        return Groups.selectAll()
            .where { Groups.id eq id }
            .map {
                Group(
                    it[Groups.id].value,
                    it[Groups.name],
                    it[Groups.description],
                    it[Groups.strategyId],
                    it[Groups.maxSize]
                )
            }
            .firstOrNull()
    }

    override fun pagination(pageRequest: PageRequest): PageResult<Group> {
        return fetchPage(Groups, pageRequest) { row ->
            Group(
                row[Groups.id].value,
                row[Groups.name],
                row[Groups.description],
                row[Groups.strategyId],
                row[Groups.maxSize]
            )
        }
    }
}