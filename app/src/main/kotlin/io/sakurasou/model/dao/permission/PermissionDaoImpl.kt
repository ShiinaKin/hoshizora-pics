package io.sakurasou.model.dao.permission

import io.sakurasou.model.dto.PermissionInsertDTO
import io.sakurasou.model.entity.Permission
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.selectAll

/**
 * @author ShiinaKin
 * 2024/9/7 14:08
 */
class PermissionDaoImpl : PermissionDao {
    override fun batchSavePermission(permissions: List<PermissionInsertDTO>) {
        Permissions.batchInsert(permissions) {
            this[Permissions.name] = it.name
            this[Permissions.description] = it.description
        }
    }

    override fun findPermissionByName(name: String): Permission? {
        return Permissions.selectAll()
            .where { Permissions.name eq name }
            .map {
                Permission(
                    it[Permissions.name],
                    it[Permissions.description]
                )
            }
            .firstOrNull()
    }
}