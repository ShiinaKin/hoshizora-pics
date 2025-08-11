package io.sakurasou.model.dao.role

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.RolePageVO
import io.sakurasou.model.dao.common.PaginationDao
import io.sakurasou.model.dto.RoleInsertDTO
import io.sakurasou.model.dto.RoleUpdateDTO
import io.sakurasou.model.entity.Role

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
