package io.sakurasou.model.dao.role

import io.sakurasou.model.dto.RoleInsertDTO

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
interface RoleDao {
    fun saveRole(roleInsertDTO: RoleInsertDTO)
}