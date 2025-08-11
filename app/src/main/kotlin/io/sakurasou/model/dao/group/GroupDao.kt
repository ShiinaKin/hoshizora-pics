package io.sakurasou.model.dao.group

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.GroupPageVO
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.model.dao.common.PaginationDao
import io.sakurasou.model.dto.GroupInsertDTO
import io.sakurasou.model.dto.GroupUpdateDTO
import io.sakurasou.model.entity.Group

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
interface GroupDao : PaginationDao {
    fun saveGroup(groupInsertDTO: GroupInsertDTO): Long

    fun deleteGroupById(id: Long): Int

    fun updateGroupById(groupUpdateDTO: GroupUpdateDTO): Int

    fun findGroupById(id: Long): Group?

    fun doesGroupUsingStrategy(strategyId: Long): Boolean

    fun pagination(pageRequest: PageRequest): PageResult<GroupPageVO>
}
