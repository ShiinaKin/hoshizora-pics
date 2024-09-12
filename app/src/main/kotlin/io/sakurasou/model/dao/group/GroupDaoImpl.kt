package io.sakurasou.model.dao.group

import io.sakurasou.model.dto.GroupInsertDTO
import io.sakurasou.model.entity.Group
import org.jetbrains.exposed.sql.insertAndGetId

/**
 * @author ShiinaKin
 * 2024/9/7 14:09
 */
class GroupDaoImpl : GroupDao {
    override fun findGroupById(id: Long): Group? {
        TODO("Not yet implemented")
    }

    override fun saveGroup(group: GroupInsertDTO): Long {
        val entityID = Groups.insertAndGetId {
            it[name] = group.name
            it[description] = group.description
            it[strategyId] = group.strategyId
            it[maxSize] = group.maxSize
        }
        return entityID.value
    }
}