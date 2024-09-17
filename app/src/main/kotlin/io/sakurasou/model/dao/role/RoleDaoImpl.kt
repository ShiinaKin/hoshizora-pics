package io.sakurasou.model.dao.role

import io.sakurasou.model.dto.RoleInsertDTO
import org.jetbrains.exposed.sql.insert

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

    override fun listRole(): List<String> {
        return Roles.select(Roles.name)
            .map { it[Roles.name] }
    }
}