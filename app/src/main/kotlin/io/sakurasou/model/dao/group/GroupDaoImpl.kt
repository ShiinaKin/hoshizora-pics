package io.sakurasou.model.dao.group

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.GroupPageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.model.dao.image.Images
import io.sakurasou.model.dto.GroupInsertDTO
import io.sakurasou.model.dto.GroupUpdateDTO
import io.sakurasou.model.entity.Group
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

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
            it[config] = groupInsertDTO.config
            it[createTime] = groupInsertDTO.createTime
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
            it[config] = groupUpdateDTO.config
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
                    it[Groups.config],
                    it[Groups.createTime]
                )
            }
            .firstOrNull()
    }

    override fun pagination(pageRequest: PageRequest): PageResult<GroupPageVO> {
        val query = { query: Query ->
            query.adjustColumnSet { join(Images, JoinType.FULL) {Images.groupId eq Groups.id} }
                .adjustSelect { select(Groups.fields + Images.id.count() + Images.size.sum()) }
                .groupBy(Groups.id, Groups.name)
        }
        return fetchPage(Groups, pageRequest, query) { row ->
            GroupPageVO(
                row[Groups.id].value,
                row[Groups.name],
                row[Groups.strategyId],
                row[Images.id.count()],
                row[Images.size.sum()]?.let {
                    it / 1024.0 / 1024.0
                } ?: 0.0
            )
        }
    }
}