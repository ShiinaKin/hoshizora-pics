package io.sakurasou.model.dao.group

import io.sakurasou.model.dto.GroupInsertDTO
import io.sakurasou.model.entity.Group

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
interface GroupDao {
    fun findGroupById(id: Long): Group?
    fun saveGroup(group: GroupInsertDTO): Long
}