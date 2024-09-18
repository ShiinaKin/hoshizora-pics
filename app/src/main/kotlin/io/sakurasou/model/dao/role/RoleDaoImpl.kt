package io.sakurasou.model.dao.role

import io.sakurasou.model.dto.RoleInsertDTO
import io.sakurasou.model.entity.Role
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
class RoleDaoImpl : RoleDao {
    override fun saveRole(roleInsertDTO: RoleInsertDTO) {
        Roles.insert {
            it[name] = roleInsertDTO.name
            it[description] = roleInsertDTO.description
        }
    }

    override fun findRoleByName(roleName: String): Role? {
        return Roles.selectAll()
            .where { Roles.name eq roleName }
            .map {
                Role(
                    it[Roles.name],
                    it[Roles.description]
                )
            }
            .firstOrNull()
    }

    override fun listRoles(): List<Role> {
        return Roles.selectAll()
            .map {
                Role(
                    it[Roles.name],
                    it[Roles.description]
                )
            }
    }
}