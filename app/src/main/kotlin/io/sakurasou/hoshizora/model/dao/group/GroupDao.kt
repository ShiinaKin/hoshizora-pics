package io.sakurasou.hoshizora.model.dao.group

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.GroupPageVO
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.model.dao.common.PaginationDao
import io.sakurasou.hoshizora.model.dto.GroupInsertDTO
import io.sakurasou.hoshizora.model.dto.GroupUpdateDTO
import io.sakurasou.hoshizora.model.entity.Group

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
