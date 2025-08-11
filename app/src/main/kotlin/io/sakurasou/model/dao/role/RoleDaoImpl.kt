package io.sakurasou.model.dao.role

import io.sakurasou.controller.request.PageRequest
import io.sakurasou.controller.vo.PageResult
import io.sakurasou.controller.vo.RolePageVO
import io.sakurasou.model.dto.RoleInsertDTO
import io.sakurasou.model.dto.RoleUpdateDTO
import io.sakurasou.model.entity.Role
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
class RoleDaoImpl : RoleDao {
    override fun saveRole(insertDTO: RoleInsertDTO) {
        Roles.insert {
            it[name] = insertDTO.name
            it[displayName] = insertDTO.displayName
            it[isSystemReserved] = insertDTO.isSystemReserved
            it[description] = insertDTO.description
            it[createTime] = insertDTO.createTime
        }
    }

    override fun patchRole(
        roleName: String,
        updateDTO: RoleUpdateDTO,
    ): Int =
        Roles.update({ Roles.name eq roleName }) {
            it[displayName] = updateDTO.displayName
            it[description] = updateDTO.description
        }

    override fun deleteRole(roleName: String): Int = Roles.deleteWhere { name eq roleName }

    override fun findRoleByName(roleName: String): Role? =
        Roles
            .selectAll()
            .where { Roles.name eq roleName }
            .map(::toRole)
            .firstOrNull()

    override fun pagination(pageRequest: PageRequest): PageResult<RolePageVO> =
        fetchPage(Roles, pageRequest) {
            RolePageVO(
                it[Roles.name],
                it[Roles.displayName],
                it[Roles.description],
            )
        }

    private fun toRole(rows: ResultRow) =
        Role(
            name = rows[Roles.name],
            displayName = rows[Roles.displayName],
            isSystemReserved = rows[Roles.isSystemReserved],
            description = rows[Roles.description],
            createTime = rows[Roles.createTime],
        )
}
