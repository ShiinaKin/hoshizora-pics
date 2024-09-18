package io.sakurasou.service.role

import io.sakurasou.controller.vo.PermissionVO
import io.sakurasou.controller.vo.RoleVO
import io.sakurasou.exception.PermissionNotExistException
import io.sakurasou.exception.RoleNotExistException
import io.sakurasou.model.DatabaseSingleton.dbQuery
import io.sakurasou.model.DatabaseSingleton.dbQueryInner
import io.sakurasou.model.dao.permission.PermissionDao
import io.sakurasou.model.dao.relation.RelationDao
import io.sakurasou.model.dao.role.RoleDao

/**
 * @author ShiinaKin
 * 2024/9/18 17:30
 */
class RoleServiceImpl(
    private val roleDao: RoleDao,
    private val permissionDao: PermissionDao,
    private val relationDao: RelationDao
) : RoleService {
    override suspend fun listRolesWithPermissions(): Map<String, RoleVO> {
        return dbQuery {
            val roles = dbQueryInner { roleDao.listRoles() }
            roles.associate { role ->
                val permissionNames = dbQueryInner { relationDao.listPermissionByRole(role.name) }
                val permissionVOList = permissionNames.map { permissionName ->
                    val permission = dbQueryInner { permissionDao.findPermissionByName(permissionName) }
                        ?: throw PermissionNotExistException()
                    PermissionVO(
                        permission.name,
                        permission.description
                    )
                }
                role.name to RoleVO(
                    role.name,
                    role.description,
                    permissionVOList
                )
            }
        }
    }

    override suspend fun listRolesWithPermissionsOfUser(roleNames: List<String>): Map<String, RoleVO> {
        return dbQuery {
            roleNames.associate { roleName ->
                val role = dbQueryInner { roleDao.findRoleByName(roleName) } ?: throw RoleNotExistException()
                val permissionNames = dbQueryInner { relationDao.listPermissionByRole(roleName) }
                val permissionVOList = permissionNames.map { permissionName ->
                    val permission = dbQueryInner { permissionDao.findPermissionByName(permissionName) }
                        ?: throw PermissionNotExistException()
                    PermissionVO(
                        permission.name,
                        permission.description
                    )
                }
                role.name to RoleVO(
                    role.name,
                    role.description,
                    permissionVOList
                )
            }
        }
    }
}