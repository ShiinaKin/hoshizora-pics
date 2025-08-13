package io.sakurasou.hoshizora.model.dao.role

import io.sakurasou.hoshizora.controller.request.PageRequest
import io.sakurasou.hoshizora.controller.vo.PageResult
import io.sakurasou.hoshizora.controller.vo.RolePageVO
import io.sakurasou.hoshizora.model.dao.common.PaginationDao
import io.sakurasou.hoshizora.model.dto.RoleInsertDTO
import io.sakurasou.hoshizora.model.dto.RoleUpdateDTO
import io.sakurasou.hoshizora.model.entity.Role

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
interface RoleDao : PaginationDao {
    fun saveRole(insertDTO: RoleInsertDTO)

    fun patchRole(
        roleName: String,
        updateDTO: RoleUpdateDTO,
    ): Int

    fun deleteRole(roleName: String): Int

    fun findRoleByName(roleName: String): Role?

    fun pagination(pageRequest: PageRequest): PageResult<RolePageVO>
}
