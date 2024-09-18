package io.sakurasou.model.dao.role

import io.sakurasou.model.dto.RoleInsertDTO
import io.sakurasou.model.entity.Role

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
interface RoleDao {
    fun saveRole(roleInsertDTO: RoleInsertDTO)
    fun findRoleByName(roleName: String): Role?
    fun listRoles(): List<Role>
}